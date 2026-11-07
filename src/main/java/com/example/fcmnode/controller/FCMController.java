package com.example.fcmnode.controller;

import com.example.fcmnode.service.FCMService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Map;

/**
 * POST /fcm/messages
 *
 * Cuerpo normal: el mensaje de FCM, {"message": {...}}. La llave se lee de fcm-key.json.
 * Cuerpo heredado: {"data": {"message": {...}}, "key": {...}}, con la llave en la petición.
 */
@RestController
public class FCMController {

    private final FCMService service;

    public FCMController(FCMService service) {
        this.service = service;
    }

    @PostMapping(value = "/messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> send(@RequestBody JsonNode body) {
        try {
            if (body.has("key")) {
                return service.send(body.path("data"), body.path("key"));
            }
            if (!body.has("message")) {
                return error(400, "El cuerpo debe ser {\"message\": {...}}, como lo pide FCM.");
            }
            var key = service.localKey();
            if (key.isEmpty()) {
                return error(500, "No encuentro la llave en " + service.keyFilePath()
                        + ". Descárgala de Firebase y guárdala con ese nombre.");
            }
            return service.send(body, key.get());
        } catch (IOException e) {
            return error(502, "No se pudo obtener el token de Google: " + e.getMessage());
        }
    }

    private ResponseEntity<Map<String, String>> error(int status, String message) {
        return ResponseEntity.status(status).body(Map.of("error", message));
    }
}

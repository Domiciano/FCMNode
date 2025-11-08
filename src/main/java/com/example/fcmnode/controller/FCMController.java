package com.example.fcmnode.controller;

import com.example.fcmnode.service.FCMService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class FCMController {

    @Autowired
    private FCMService service;

    @PostMapping(value = "messages", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> sendWithJsonKey(@RequestBody String data){
        try {
            var mapper = new ObjectMapper();
            var root = mapper.readTree(data);
            var message = root.path("data").toString();
            System.out.println(message);

            var keyNode = root.path("key");
            var token = service.getAccessTokenUsingKey(keyNode.toString());
            System.out.println(token);
            var projectId = root.path("key").path("project_id").asText();
            System.out.println(projectId);
            var response = service.POSTtoFCM(message.toString(), token, projectId);
            return ResponseEntity.status(200).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

}
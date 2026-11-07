package com.example.fcmnode.service;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Obtiene un token de acceso a partir de la llave de la cuenta de servicio
 * y reenvía el mensaje a la API v1 de FCM.
 */
@Service
public class FCMService {

    private static final String SCOPE = "https://www.googleapis.com/auth/firebase.messaging";
    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/{projectId}/messages:send";

    private final RestClient restClient = RestClient.create();
    private final JsonMapper mapper = JsonMapper.builder().build();
    private final Path keyFile;

    public FCMService(@Value("${fcm.key-file}") String keyFile) {
        this.keyFile = Path.of(keyFile);
    }

    /** La llave del archivo configurado en fcm.key-file, si existe. */
    public Optional<JsonNode> localKey() throws IOException {
        if (!Files.exists(keyFile)) {
            return Optional.empty();
        }
        return Optional.of(mapper.readTree(Files.readString(keyFile)));
    }

    public String keyFilePath() {
        return keyFile.toAbsolutePath().toString();
    }

    /** Envía el cuerpo {"message": {...}} a FCM y devuelve lo que FCM responde. */
    public ResponseEntity<String> send(JsonNode body, JsonNode key) throws IOException {
        var projectId = key.path("project_id").asString();
        var token = accessToken(key);
        try {
            return restClient.post()
                    .uri(FCM_URL, projectId)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body.toString())
                    .retrieve()
                    .toEntity(String.class);
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    private String accessToken(JsonNode key) throws IOException {
        var json = new ByteArrayInputStream(key.toString().getBytes(StandardCharsets.UTF_8));
        var credentials = GoogleCredentials.fromStream(json).createScoped(List.of(SCOPE));
        credentials.refreshIfExpired();
        return credentials.getAccessToken().getTokenValue();
    }
}

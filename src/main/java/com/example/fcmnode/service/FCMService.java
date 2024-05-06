package com.example.fcmnode.service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class FCMService {
    private static final String PROJECT_NAME = "facelogprueba";
    private static final String KEY_FILE_NAME = "fcmkey.json";

    public static String getAccessToken() throws IOException {
        ClassPathResource resource = new ClassPathResource(KEY_FILE_NAME);
        InputStream inputStream = resource.getInputStream();
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();
        AccessToken token = googleCredentials.getAccessToken();
        return token.getTokenValue();
    }

    public static String POSTtoFCM(String json, String FCM_KEY) throws IOException {
        URL url = URI.create("https://fcm.googleapis.com/v1/projects/"+PROJECT_NAME+"/messages:send").toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; UTF-8");
        connection.setRequestProperty("Authorization", "Bearer " + FCM_KEY);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8))) {
            writer.write(json);
            writer.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode >= 200 && responseCode < 300) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                throw new IOException("HTTP Error: " + responseCode + ", " + errorResponse.toString());
            }
        }
    }


}

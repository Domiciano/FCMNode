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

    public static String getAccessToken() throws IOException {
        ClassPathResource resource = new ClassPathResource("fcmkey2.json");
        InputStream inputStream = resource.getInputStream();
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(inputStream)
                .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
        googleCredentials.refreshIfExpired();
        AccessToken token = googleCredentials.getAccessToken();
        return token.getTokenValue();
    }

    public static String POSTtoFCM(String json, String FCM_KEY) throws IOException {
        URL page = URI.create("https://fcm.googleapis.com/v1/projects/facelogprueba/messages:send").toURL();
        HttpsURLConnection connection = (HttpsURLConnection) page.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; UTF-8");
        connection.setRequestProperty("Authorization", "Bearer " + FCM_KEY);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        writer.write(json);
        writer.flush();
        if (("" + connection.getResponseCode()).startsWith("2")) {
            return connection.getResponseMessage();
        } else {
            String out = new String(connection.getErrorStream().readAllBytes());
            throw new IOException(out);
        }
    }

}

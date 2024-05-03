package com.example.fcmnode.controller;

import com.example.fcmnode.service.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class FCMController {

    private FCMService service = new FCMService();

    @PostMapping("fcm/send")
    public ResponseEntity<?> send(@RequestBody String data){
        try {
            var token = service.getAccessToken();
            String response = service.POSTtoFCM(data, token);
            return ResponseEntity.status(200).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(200).body(e.getMessage());
        }
    }

    @GetMapping("hello")
    public String send(){return "Hello";}

}

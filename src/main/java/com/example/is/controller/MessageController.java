package com.example.is.controller;

import com.example.is.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController

@RequestMapping("/chat")
public class MessageController {

    private final MessageService messageService;


    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> payload) {
        try {
            String sender = payload.get("sender");
            String receiver = payload.get("receiver");
            String message = payload.get("message");

            messageService.sendMessage(sender, receiver, message);
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending message: " + e.getMessage());
        }
    }



    @GetMapping("/history")
    public ResponseEntity<?> getChat(
            @RequestParam String user1,
            @RequestParam String user2,
            @RequestParam(required = false) String viewer) {
        try {
            if (viewer == null) {

                viewer = user1; // fallback to default
            }

            List<String> chat = messageService.getChat(user1, user2, viewer);
            return ResponseEntity.ok(chat);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching chat: " + e.getMessage());
        }
    }


    


}

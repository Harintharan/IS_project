package com.example.is.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private String receiver;
    private String encryptedMessage;
    private long timestamp;
    private String hmac;
}


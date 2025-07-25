
package com.example.is.service;

import com.example.is.crypto.SecureMessageUtils;
import com.example.is.model.AuditLog;
import com.example.is.model.ChatMessage;
import com.example.is.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private AuditLogRepository auditLogRepository;


    private final List<ChatMessage> messageList = new ArrayList<>();
    private final ActiveUserService activeUserService;

    public MessageService(ActiveUserService activeUserService) {
        this.activeUserService = activeUserService;
    }

    public void sendMessage(String sender, String receiver, String plainText) throws Exception {
        PublicKey receiverKey = activeUserService.getPublicKey(receiver);
        String encryptedMessage = SecureMessageUtils.encryptMessage(plainText, receiverKey);
        String hmac = SecureMessageUtils.generateHMAC(encryptedMessage);
        long timestamp = System.currentTimeMillis();

        System.out.println("🔐 Encrypted message from " + sender + " to " + receiver + ": " + encryptedMessage);
        System.out.println("🔑 HMAC: " + hmac);
        System.out.println("🕒 Timestamp: " + timestamp);

        ChatMessage message = new ChatMessage(sender, receiver, encryptedMessage, timestamp, hmac);
        messageList.add(message);
    }


    private final Set<String> processedMessageHashes = new HashSet<>();

    public List<String> getChat(String user1, String user2, String viewer) {
        return messageList.stream()
                .filter(m ->
                        (m.getSender().equals(user1) && m.getReceiver().equals(user2)) ||
                                (m.getSender().equals(user2) && m.getReceiver().equals(user1))
                )
                .map(m -> {
                    try {
                        String sender = m.getSender();
                        String receiver = m.getReceiver();
                        String encrypted = m.getEncryptedMessage();
                        String hmac = m.getHmac();

                        // Unique identifier for the message
                        String signature = sender + "|" + receiver + "|" + hmac;

                        if (!processedMessageHashes.contains(signature)) {
                            processedMessageHashes.add(signature); // mark as processed

                            System.out.println("\n🔄 Processing message:");
                            System.out.println("👤 Sender: " + sender);
                            System.out.println("🎯 Receiver: " + receiver);
                            System.out.println("🔐 Encrypted: " + encrypted);
                            System.out.println("🔑 HMAC: " + hmac);

                            boolean hmacValid = SecureMessageUtils.verifyHMAC(encrypted, hmac);
                            System.out.println("✅ HMAC Valid: " + hmacValid);

                            if (!hmacValid) {
                                return sender + ": ❌ [Tampered message]";
                            }

                            if (viewer.equals(receiver)) {


                                PrivateKey receiverPrivateKey = activeUserService.getPrivateKey(receiver);
                                String decrypted = SecureMessageUtils.decryptMessage(encrypted, receiverPrivateKey);
                                System.out.println("✅ Decrypted message: " + decrypted);





                                return sender + ": " + decrypted;
                            } else {
                                System.out.println("ℹ️ Viewer is not receiver, showing encrypted");
                                return sender + ": 🔐 [Encrypted]";
                            }
                        } else {
                            // Already processed — just return without logging
                            if (viewer.equals(receiver)) {
                                PrivateKey receiverPrivateKey = activeUserService.getPrivateKey(receiver);
                                String decrypted = SecureMessageUtils.decryptMessage(encrypted, receiverPrivateKey);
                                return sender + ": " + decrypted;
                            } else {
                                return sender + ": 🔐 [Encrypted]";
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Error processing message: " + e.getMessage());
                        return m.getSender() + ": ⚠️ [Error Decrypting]";
                    }
                })
                .collect(Collectors.toList());
    }




}

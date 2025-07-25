//package com.example.is.service;
//
//import com.example.is.crypto.EncryptionUtils;
//import com.example.is.model.ChatMessage;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class MessageService {
//
//    private final List<ChatMessage> messageList = new ArrayList<>();
//
//    public void sendMessage(String sender, String receiver, String plainText) throws Exception {
//        String encrypted = EncryptionUtils.encrypt(plainText);
//        ChatMessage message = new ChatMessage(sender, receiver, encrypted, System.currentTimeMillis());
//        messageList.add(message);
//    }
//
//    public List<String> getChat(String user1, String user2) throws Exception {
//        return messageList.stream()
//                .filter(m ->
//                        (m.getSender().equals(user1) && m.getReceiver().equals(user2)) ||
//                                (m.getSender().equals(user2) && m.getReceiver().equals(user1))
//                )
//                .map(m -> {
//                    try {
//                        return m.getSender() + ": " + EncryptionUtils.decrypt(m.getEncryptedMessage());
//                    } catch (Exception e) {
//                        return m.getSender() + ": [Error Decrypting]";
//                    }
//                })
//                .collect(Collectors.toList());
//    }
//
//
//
//}


package com.example.is.service;

import com.example.is.crypto.SecureMessageUtils;
import com.example.is.model.ChatMessage;
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

//    public List<String> getChat(String user1, String user2) throws Exception {
//        PrivateKey privateKey = activeUserService.getPrivateKey(user1); // Assuming user1 is viewing
//
//        return messageList.stream()
//                .filter(m -> (m.getSender().equals(user1) && m.getReceiver().equals(user2)) ||
//                        (m.getSender().equals(user2) && m.getReceiver().equals(user1)))
//                .map(m -> {
//                    try {
//                        boolean validHmac = SecureMessageUtils.verifyHMAC(m.getEncryptedMessage(), m.getHmac());
//                        if (!validHmac) {
//                            System.out.println("⚠️ HMAC verification failed for message from " + m.getSender());
//                            return m.getSender() + ": ❌ [Tampered Message]";
//                        }
//
//                        String decrypted = SecureMessageUtils.decryptMessage(m.getEncryptedMessage(), privateKey);
//                        System.out.println("✅ Decrypted message for " + user1 + ": " + decrypted);
//                        return m.getSender() + ": " + decrypted;
//                    } catch (Exception e) {
//                        System.out.println("❌ Error decrypting message: " + e.getMessage());
//                        return m.getSender() + ": ⚠️ [Error Decrypting]";
//                    }
//                }).collect(Collectors.toList());
//    }

//    public List<String> getChat(String user1, String user2) {
//        return messageList.stream()
//                .filter(m ->
//                        (m.getSender().equals(user1) && m.getReceiver().equals(user2)) ||
//                                (m.getSender().equals(user2) && m.getReceiver().equals(user1))
//                )
//                .map(m -> {
//                    try {
//                        String encrypted = m.getEncryptedMessage();
//
//                        // ✅ Verify HMAC
//                        if (!SecureMessageUtils.verifyHMAC(encrypted, m.getHmac())) {
//                            return m.getSender() + ": ❌ [Tampered message]";
//                        }
//
//                        // ✅ Only decrypt if receiver is the logged-in user
//                        if (m.getReceiver().equals(user1)) {
//                            PrivateKey receiverPrivateKey = activeUserService.getPrivateKey(user1);
//                            if (receiverPrivateKey == null) return m.getSender() + ": ❌ [Missing Key]";
//                            String decrypted = SecureMessageUtils.decryptMessage(encrypted, receiverPrivateKey);
//                            return m.getSender() + ": " + decrypted;
//                        } else {
//                            // Sender — no need to decrypt their own sent message
//                            return m.getSender() + ": 🔐 [Encrypted]";
//                        }
//                    } catch (Exception e) {
//                        return m.getSender() + ": ⚠️ [Error Decrypting]";
//                    }
//                }).collect(Collectors.toList());
//    }

//    public List<String> getChat(String user1, String user2) {
//        return messageList.stream()
//                .filter(m ->
//                        (m.getSender().equals(user1) && m.getReceiver().equals(user2)) ||
//                                (m.getSender().equals(user2) && m.getReceiver().equals(user1))
//                )
//                .map(m -> {
//                    try {
//                        String encrypted = m.getEncryptedMessage();
//                        String hmac = m.getHmac();
//                        String sender = m.getSender();
//                        String receiver = m.getReceiver();
//
//                        System.out.println("\n🔄 Processing message:");
//                        System.out.println("👤 Sender: " + sender);
//                        System.out.println("🎯 Receiver: " + receiver);
//                        System.out.println("🔐 Encrypted: " + encrypted);
//                        System.out.println("🔑 HMAC: " + hmac);
//
//                        // ✅ Step 1: Verify HMAC
//                        boolean hmacValid = SecureMessageUtils.verifyHMAC(encrypted, hmac);
//                        System.out.println("✅ HMAC Valid: " + hmacValid);
//                        if (!hmacValid) {
//                            return sender + ": ❌ [Tampered message]";
//                        }
//
//                        // ✅ Step 2: Determine if user1 is receiver (i.e., should decrypt)
//                        if (receiver.equals(user1)) {
//                            PrivateKey receiverPrivateKey = activeUserService.getPrivateKey(user1);
//                            if (receiverPrivateKey == null) {
//                                System.out.println("❌ Missing private key for user: " + user1);
//                                return sender + ": ❌ [Missing Key]";
//                            }
//
//                            // ✅ Step 3: Decrypt
//                            String decrypted = SecureMessageUtils.decryptMessage(encrypted, receiverPrivateKey);
//                            System.out.println("✅ Decrypted message: " + decrypted);
//                            return sender + ": " + decrypted;
//                        } else {
//                            // Sender side — don’t decrypt
//                            System.out.println("ℹ️ Not decrypting (viewer is sender), showing encrypted");
//                            return sender + ": 🔐 [Encrypted]";
//                        }
//
//                    } catch (Exception e) {
//                        System.out.println("❌ Error decrypting: " + e.getMessage());
//                        e.printStackTrace();
//                        return m.getSender() + ": ⚠️ [Error Decrypting]";
//                    }
//                })
//                .collect(Collectors.toList());
//    }

//    public List<String> getChat(String user1, String user2, String viewer) {
//        return messageList.stream()
//                .filter(m ->
//                        (m.getSender().equals(user1) && m.getReceiver().equals(user2)) ||
//                                (m.getSender().equals(user2) && m.getReceiver().equals(user1))
//                )
//                .map(m -> {
//                    try {
//                        String encrypted = m.getEncryptedMessage();
//                        String hmac = m.getHmac();
//                        String sender = m.getSender();
//                        String receiver = m.getReceiver();
//
//                        // Only log once per message: when viewed by the receiver
//                        if (!viewer.equals(receiver)) {
//                            return sender + ": 🔐 [Encrypted]";
//                        }
//
//                        System.out.println("\n🔄 Processing message:");
//                        System.out.println("👤 Sender: " + sender);
//                        System.out.println("🎯 Receiver: " + receiver);
//                        System.out.println("🔐 Encrypted: " + encrypted);
//                        System.out.println("🔑 HMAC: " + hmac);
//
//                        boolean hmacValid = SecureMessageUtils.verifyHMAC(encrypted, hmac);
//                        System.out.println("✅ HMAC Valid: " + hmacValid);
//                        if (!hmacValid) {
//                            return sender + ": ❌ [Tampered message]";
//                        }
//
//                        PrivateKey receiverPrivateKey = activeUserService.getPrivateKey(receiver);
//                        if (receiverPrivateKey == null) {
//                            System.out.println("❌ Missing private key for user: " + receiver);
//                            return sender + ": ❌ [Missing Key]";
//                        }
//
//                        String decrypted = SecureMessageUtils.decryptMessage(encrypted, receiverPrivateKey);
//                        System.out.println("✅ Decrypted message: " + decrypted);
//                        return sender + ": " + decrypted;
//
//                    } catch (Exception e) {
//                        System.out.println("❌ Error decrypting: " + e.getMessage());
//                        return m.getSender() + ": ⚠️ [Error Decrypting]";
//                    }
//                })
//                .collect(Collectors.toList());
//    }

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

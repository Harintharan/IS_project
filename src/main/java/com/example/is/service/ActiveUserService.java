
package com.example.is.service;

import com.example.is.model.AuditLog;
import com.example.is.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveUserService {

    private final Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, PublicKey> publicKeys = new ConcurrentHashMap<>();
    private final Map<String, PrivateKey> privateKeys = new ConcurrentHashMap<>();

    private final AuditLogRepository auditLogRepository;

    public ActiveUserService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void login(String username) {
        activeUsers.add(username);
        generateKeyPair(username);
        logAudit(username, "LOGIN");
        System.out.println("✅ " + username + " logged in");
    }

    public void logout(String username) {
        activeUsers.remove(username);
        publicKeys.remove(username);
        privateKeys.remove(username);
        logAudit(username, "LOGOUT");
        System.out.println("🚪 " + username + " logged out");
    }

    public Set<String> getActiveUsers() {
        synchronized (activeUsers) {
            return new HashSet<>(activeUsers);
        }
    }

    public PublicKey getPublicKey(String username) {
        return publicKeys.get(username);
    }

    public PrivateKey getPrivateKey(String username) {
        PrivateKey key = privateKeys.get(username);
        if (key == null) {
            System.out.println("❌ Private key for user '" + username + "' is NULL");
        }
        return key;
    }

    private void generateKeyPair(String username) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();

            publicKeys.put(username, pair.getPublic());
            privateKeys.put(username, pair.getPrivate());

            System.out.println("🔑 Keys generated for user: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Key generation failed for " + username, e);
        }
    }

    private void logAudit(String username, String action) {
        try {
            AuditLog log = new AuditLog();
            log.setUsername(username);
            log.setAction(action);
            log.setTimestamp(System.currentTimeMillis());

            auditLogRepository.save(log);
            System.out.println("📝 Audit log saved: " + username + " - " + action);
        } catch (Exception e) {
            System.out.println("❌ Failed to save audit log: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

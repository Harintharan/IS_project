//package com.example.is.service;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import org.springframework.stereotype.Service;
//
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//public class ActiveUserService {
//
//    private final Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());
//
//    public void login(String username) {
//        System.out.println("User logged in: " + username); // <-- Add this log
//        activeUsers.add(username);
//    }
//
//    public void logout(String username) {
//        System.out.println("User logged out: " + username);
//        activeUsers.remove(username);
//    }
//
//    public Set<String> getActiveUsers() {
//        synchronized (activeUsers) {
//            return new HashSet<>(activeUsers); // or use Set.copyOf(activeUsers) if Java 10+
//        }
//    }
//
//
//    @PostConstruct
//    public void init() {
//        activeUsers.clear();
//    }
//
//    @PreDestroy
//    public void destroy() {
//        activeUsers.clear();
//    }
//}


package com.example.is.service;

import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveUserService {
    private final Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());
    private final Map<String, PublicKey> publicKeys = new ConcurrentHashMap<>();
    private final Map<String, PrivateKey> privateKeys = new ConcurrentHashMap<>();

    public void login(String username) {
        activeUsers.add(username);
        generateKeyPair(username);
    }

    public void logout(String username) {
        activeUsers.remove(username);
        publicKeys.remove(username);
        privateKeys.remove(username);
    }

    public Set<String> getActiveUsers() {
        synchronized (activeUsers) {
            return new HashSet<>(activeUsers);
        }
    }

    public PublicKey getPublicKey(String username) {
        return publicKeys.get(username);
    }

//    public PrivateKey getPrivateKey(String username) {
//        return privateKeys.get(username);
//    }

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
}

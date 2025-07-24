package com.example.is.crypto;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class SecureMessageUtils {
    public static final String HMAC_SECRET = "super-secret-key"; // Store securely!

    public static String encryptMessage(String message, PublicKey receiverKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, receiverKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decryptMessage(String encrypted, PrivateKey receiverPrivateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, receiverPrivateKey);
        byte[] decoded = Base64.getDecoder().decode(encrypted);
        return new String(cipher.doFinal(decoded));
    }

    public static String generateHMAC(String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(HMAC_SECRET.getBytes(), "HmacSHA256");
        hmac.init(secretKey);
        return Base64.getEncoder().encodeToString(hmac.doFinal(data.getBytes()));
    }

    public static boolean verifyHMAC(String data, String hmacToCompare) throws Exception {
        String generated = generateHMAC(data);
        return generated.equals(hmacToCompare);
    }
}

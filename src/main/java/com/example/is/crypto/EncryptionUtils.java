package com.example.is.crypto;

import javax.crypto.Cipher;

import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;

public class EncryptionUtils {

    private static final String ENCRYPTION_ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 128;
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH_BIT = 128;


    private static final String STATIC_SECRET = "1234567890123456";

    public static String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey key = new SecretKeySpec(STATIC_SECRET.getBytes(), ENCRYPTION_ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        byte[] encryptedIvAndText = new byte[iv.length + encrypted.length];

        System.arraycopy(iv, 0, encryptedIvAndText, 0, iv.length);
        System.arraycopy(encrypted, 0, encryptedIvAndText, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(encryptedIvAndText);
    }

    public static String decrypt(String encryptedText) throws Exception {
        byte[] decode = Base64.getDecoder().decode(encryptedText);
        byte[] iv = new byte[IV_SIZE];
        byte[] encrypted = new byte[decode.length - IV_SIZE];

        System.arraycopy(decode, 0, iv, 0, iv.length);
        System.arraycopy(decode, iv.length, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKey key = new SecretKeySpec(STATIC_SECRET.getBytes(), ENCRYPTION_ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted);
    }
}

package com.example.barta_a_messenger_app.helpers;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class EncryptionHelper {

    // AES constants
    private static final int AES_KEY_SIZE = 256; // bits
    private static final int GCM_IV_LENGTH = 12; // bytes
    private static final int GCM_TAG_LENGTH = 128; // bits

    // Encrypts a message using AES, and AES key is encrypted with both receiver's and sender's public keys
    public static EncryptedPayload encryptMessage(String message, String base64ReceiverPublicKey, String base64SenderPublicKey) throws Exception {
        // Generate AES key
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(AES_KEY_SIZE);
        SecretKey aesKey = keyGen.generateKey();

        // Generate IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        // Encrypt message with AES/GCM
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, spec);
        byte[] encryptedMsg = aesCipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

        // Encrypt AES key with receiver's public key
        PublicKey receiverPublicKey = getPublicKeyFromBase64(base64ReceiverPublicKey);
        Cipher rsaCipherReceiver = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipherReceiver.init(Cipher.ENCRYPT_MODE, receiverPublicKey);
        byte[] encryptedAESKey = rsaCipherReceiver.doFinal(aesKey.getEncoded());

        // Encrypt AES key with sender's public key
        PublicKey senderPublicKey = getPublicKeyFromBase64(base64SenderPublicKey);
        Cipher rsaCipherSender = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipherSender.init(Cipher.ENCRYPT_MODE, senderPublicKey);
        byte[] encryptedAESKeyForSender = rsaCipherSender.doFinal(aesKey.getEncoded());

        // Return dual encrypted payload
        return new EncryptedPayload(
                Base64.encodeToString(encryptedMsg, Base64.NO_WRAP),
                Base64.encodeToString(encryptedAESKey, Base64.NO_WRAP),
                Base64.encodeToString(iv, Base64.NO_WRAP),
                Base64.encodeToString(encryptedAESKeyForSender, Base64.NO_WRAP)
        );
    }

    // Decrypts AES key using RSA private key and then decrypts the message
    public static String decryptMessage(String encryptedMessage, String encryptedAESKey, String iv) throws Exception {
        PrivateKey privateKey = KeyStoreHelper.getPrivateKey();

        // Decrypt AES key with RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] aesKeyBytes = rsaCipher.doFinal(Base64.decode(encryptedAESKey, Base64.NO_WRAP));

        SecretKeySpec aesKey = new SecretKeySpec(aesKeyBytes, "AES");

        // Decrypt message using AES/GCM
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, Base64.decode(iv, Base64.NO_WRAP));
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, spec);

        byte[] decrypted = aesCipher.doFinal(Base64.decode(encryptedMessage, Base64.NO_WRAP));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // Utility to convert base64 to PublicKey
    private static PublicKey getPublicKeyFromBase64(String base64Key) throws Exception {
        byte[] decoded = Base64.decode(base64Key, Base64.DEFAULT);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Wrapper class for encrypted message payload
    public static class EncryptedPayload {
        public String encryptedMessage;
        public String encryptedAESKey;
        public String iv;

        // New: sender-side encrypted AES key
        public String encryptedAESKeyForSender;

        public EncryptedPayload(String encryptedMessage, String encryptedAESKey, String iv, String encryptedAESKeyForSender) {
            this.encryptedMessage = encryptedMessage;
            this.encryptedAESKey = encryptedAESKey;
            this.iv = iv;
            this.encryptedAESKeyForSender = encryptedAESKeyForSender;
        }
    }
}

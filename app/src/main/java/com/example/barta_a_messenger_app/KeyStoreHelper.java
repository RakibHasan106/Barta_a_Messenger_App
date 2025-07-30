package com.example.barta_a_messenger_app;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAKeyGenParameterSpec;

public class KeyStoreHelper {

    private static final String KEY_ALIAS = "barta_key";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    // Generates a key pair only if it does not already exist
    public static void generateKeyPairIfNotExists() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE);

                KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
                )
                        .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                        .build();

                keyPairGenerator.initialize(keyGenParameterSpec);
                keyPairGenerator.generateKeyPair();

                Log.d("KeyStoreHelper", "RSA Key pair generated successfully.");
            }
        } catch (Exception e) {
            Log.e("KeyStoreHelper", "Key pair generation failed: " + e.getMessage(), e);
        }
    }

    // Returns the public key as a Base64 encoded string
    public static String getEncodedPublicKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();
            return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("KeyStoreHelper", "Failed to retrieve public key: " + e.getMessage(), e);
            return null;
        }
    }

    // Returns the private key for decryption use (not encoded)
    public static PrivateKey getPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            return (PrivateKey) keyStore.getKey(KEY_ALIAS, null);
        } catch (Exception e) {
            Log.e("KeyStoreHelper", "Failed to retrieve private key: " + e.getMessage(), e);
            return null;
        }
    }

    public static String getPublicKeyBase64() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);
            PublicKey publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();
            return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
        } catch (Exception e) {
            Log.e("KeyStoreHelper", "Failed to retrieve public key: " + e.getMessage(), e);
            return null;
        }
    }

}

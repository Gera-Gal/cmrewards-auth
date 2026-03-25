package com.praxis.authentication.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtil {

	private static final String AES = "AES";
    private static final String AES_GCM_NOPADDING = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final int IV_LENGTH_BYTES = 12; // 96 bits recommended
    private static final String KEY = "5m#Ns2.Rjz8$ptj.4VwX5&.Y7qjR$";

    private static byte[] deriveKeyFromPassword(String password) throws Exception {
        // Deriva una clave de 256 bits usando SHA-256 (simple). Para mayor seguridad usar PBKDF2.
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        return sha.digest(password.getBytes(StandardCharsets.UTF_8));
    }

    public static String encrypt(String plainText) throws Exception {
        byte[] keyBytes = deriveKeyFromPassword(KEY);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES);

        byte[] iv = new byte[IV_LENGTH_BYTES];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);

        byte[] cipherBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // Resultado = IV + CIPHERTEXT (GCM incluye tag)
        ByteBuffer bb = ByteBuffer.allocate(iv.length + cipherBytes.length);
        bb.put(iv);
        bb.put(cipherBytes);
        byte[] out = bb.array();
        return Base64.getEncoder().encodeToString(out);
    }

    public static String decrypt(String base64IvCiphertext) throws Exception {
        byte[] all = Base64.getDecoder().decode(base64IvCiphertext);

        byte[] iv = new byte[IV_LENGTH_BYTES];
        System.arraycopy(all, 0, iv, 0, IV_LENGTH_BYTES);
        int cipherLen = all.length - IV_LENGTH_BYTES;
        byte[] cipherBytes = new byte[cipherLen];
        System.arraycopy(all, IV_LENGTH_BYTES, cipherBytes, 0, cipherLen);

        byte[] keyBytes = deriveKeyFromPassword(KEY);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, AES);

        Cipher cipher = Cipher.getInstance(AES_GCM_NOPADDING);
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

        byte[] plain = cipher.doFinal(cipherBytes);
        return new String(plain, StandardCharsets.UTF_8);
    }

    // Opcional: generar una clave aleatoria segura (solo para referencia)
    public static String generateRandomKeyBase64(int bits) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(bits);
        byte[] key = kg.generateKey().getEncoded();
        return Base64.getEncoder().encodeToString(key);
    }
    
}

package com.example.traveldiary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Login, sign-up password One-way SHA-256 encryption algorithm applied.
 * <pre>
 *     {@code
 *          SHA256 sha256 = new SHA256();
 *          sha256.encrypt(password); }
 * </pre>
 *
 * @author MIN
 * @since 1.0
 */
public class SHA256 {
    public String encrypt(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes());

        return bytesToHex(md.digest());
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes)
            builder.append(String.format("%02x", b));
        return builder.toString();
    }
}
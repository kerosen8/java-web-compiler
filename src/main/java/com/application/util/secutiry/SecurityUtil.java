package com.application.util.secutiry;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.*;

@UtilityClass
public class SecurityUtil {

    public static byte[] hexStringToByteArray(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return byteArrayToHexString(salt);
    }

    @SneakyThrows
    public static String generateHash(String password, String salt) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(hexStringToByteArray(salt));
        byte[] hash = md.digest(password.getBytes(UTF_8));
        return byteArrayToHexString(hash);
    }

    @SneakyThrows
    public static boolean verifyHash(String password, String hash, String salt) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        md.update(hexStringToByteArray(salt));
        byte[] passwordHashBytes = md.digest(password.getBytes(UTF_8));
        byte[] hashBytes = hexStringToByteArray(hash);
        return MessageDigest.isEqual(passwordHashBytes, hashBytes);
    }

    public static String generateFilePath() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

}

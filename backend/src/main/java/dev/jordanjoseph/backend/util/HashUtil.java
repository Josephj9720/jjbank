package dev.jordanjoseph.backend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            //convert to bytes to HEX -
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length); //256-bits = 32 bytes, 2 chars per bytes = 64 chars
            for (byte hashByte : hashBytes) {
                //(0xff &): sign extended byte when converted to int (32 bits), remove extra bits to get unsigned value range 0-255
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append(0);
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException("SHA-256 is not available", e);
        }
    }
    
}

package com.alkemy.java2.clase15.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class CryptoUtil {
  private static final String SECRET_KEY = "1234567812345678"; // 16 caracteres

  public static String encrypt(String data) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
  }

  public static String decrypt(String encryptedData) throws Exception {
    Cipher cipher = Cipher.getInstance("AES");
    SecretKeySpec key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
    cipher.init(Cipher.DECRYPT_MODE, key);
    return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedData)));
  }
}
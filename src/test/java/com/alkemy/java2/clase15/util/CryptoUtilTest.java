package com.alkemy.java2.clase15.util;


import com.alkemy.java2.clase15.util.CryptoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import javax.crypto.IllegalBlockSizeException;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilTest {

  private String testData;
  private String encryptedTestData;

  @BeforeEach
  void setUp() throws Exception {
    testData = "Test data to encrypt 123!@#";
    encryptedTestData = CryptoUtil.encrypt(testData);
  }

  @Test
  @DisplayName("Encrypt should return different output than input")
  void encrypt_ShouldReturnDifferentOutputThanInput() throws Exception {
    // Arrange is done in setUp()

    // Act
    String encrypted = CryptoUtil.encrypt(testData);

    // Assert
    assertNotEquals(testData, encrypted);
    assertNotNull(encrypted);
  }

  @Test
  @DisplayName("Decrypt should return original data when given encrypted data")
  void decrypt_ShouldReturnOriginalData_WhenGivenEncryptedData() throws Exception {
    // Arrange is done in setUp()

    // Act
    String decrypted = CryptoUtil.decrypt(encryptedTestData);

    // Assert
    assertEquals(testData, decrypted);
  }

  @Test
  @DisplayName("Encrypt and decrypt should work with empty string")
  void encryptAndDecrypt_ShouldWorkWithEmptyString() throws Exception {
    // Arrange
    String emptyString = "";

    // Act
    String encrypted = CryptoUtil.encrypt(emptyString);
    String decrypted = CryptoUtil.decrypt(encrypted);

    // Assert
    assertEquals(emptyString, decrypted);
  }

  @ParameterizedTest
  @ValueSource(strings = {" ", "a", "A", "1", "!", "áéíóú", "你好"})
  @DisplayName("Encrypt and decrypt should work with edge case characters")
  void encryptAndDecrypt_ShouldWorkWithEdgeCaseCharacters(String input) throws Exception {
    // Act
    String encrypted = CryptoUtil.encrypt(input);
    String decrypted = CryptoUtil.decrypt(encrypted);

    // Assert
    assertEquals(input, decrypted);
  }

  @Test
  @DisplayName("Encrypt should throw exception when data is null")
  void encrypt_ShouldThrowException_WhenDataIsNull() {
    // Arrange
    String nullData = null;

    // Act & Assert
    Exception exception = assertThrows(
        Exception.class,
        () -> CryptoUtil.encrypt(nullData)
    );

    assertTrue(exception instanceof NullPointerException ||
        exception instanceof IllegalBlockSizeException);
  }

  @Test
  @DisplayName("Decrypt should throw exception when encrypted data is null")
  void decrypt_ShouldThrowException_WhenEncryptedDataIsNull() {
    // Arrange
    String nullData = null;

    // Act & Assert
    Exception exception = assertThrows(
        Exception.class,
        () -> CryptoUtil.decrypt(nullData)
    );

    assertTrue(exception instanceof NullPointerException ||
        exception instanceof IllegalArgumentException);
  }

  @Test
  @DisplayName("Decrypt should throw exception when encrypted data is malformed")
  void decrypt_ShouldThrowException_WhenEncryptedDataIsMalformed() {
    // Arrange
    String malformedData = "NotBase64Encoded!@#";

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class,
        () -> CryptoUtil.decrypt(malformedData)
    );
  }

  @Test
  @DisplayName("Decrypt should throw exception when encrypted data is tampered with")
  void decrypt_ShouldThrowException_WhenEncryptedDataIsTamperedWith() {
    // Arrange
    String tamperedData = encryptedTestData.substring(0, encryptedTestData.length() - 2) + "==";

    // Act & Assert
    assertThrows(
        Exception.class,
        () -> CryptoUtil.decrypt(tamperedData)
    );
  }

  @Test
  @DisplayName("Encrypt should produce same output for same input in ECB mode")
  void encrypt_ShouldProduceSameOutputs_ForSameInput() throws Exception {
    // Act
    String encrypted1 = CryptoUtil.encrypt(testData);
    String encrypted2 = CryptoUtil.encrypt(testData);

    // Assert - ECB mode should produce identical output
    assertEquals(encrypted1, encrypted2);
  }

  @Test
  @DisplayName("Very long input should be encrypted and decrypted correctly")
  void veryLongInput_ShouldBeEncryptedAndDecryptedCorrectly() throws Exception {
    // Arrange
    StringBuilder longStringBuilder = new StringBuilder();
    for (int i = 0; i < 10000; i++) {
      longStringBuilder.append("a");
    }
    String longString = longStringBuilder.toString();

    // Act
    String encrypted = CryptoUtil.encrypt(longString);
    String decrypted = CryptoUtil.decrypt(encrypted);

    // Assert
    assertEquals(longString, decrypted);
  }
}

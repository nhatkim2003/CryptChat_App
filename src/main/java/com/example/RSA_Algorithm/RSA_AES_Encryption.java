package com.example.RSA_Algorithm;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

public class RSA_AES_Encryption {
    public static SecretKey aesKey;
    // Tạo khóa AES
    public static void generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Độ dài khóa có thể là 128, 192, hoặc 256 bit
        aesKey = keyGen.generateKey();
        System.out.println("Khoá AES (" + Arrays.toString(aesKey.getEncoded()) +")" );
    }

    // Mã hóa nội dung tệp bằng AES
    public static void encryptFileWithAES(File inputFile, SecretKey aesKey, String outputFilePath) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFilePath)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] encryptedBlock = cipher.update(buffer, 0, bytesRead);
                if (encryptedBlock != null) {
                    fos.write(encryptedBlock);
                }
            }
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                fos.write(finalBlock);
            }
        }
    }

    // Mã hóa nội dung tệp bằng AES
    public static byte[] encryptAudioWithAES(byte[] buffer, int bytesRead, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, aesKey);
        return cipher.update(buffer, 0, bytesRead);

    }

    // Mã hóa khóa AES bằng RSA
    public static List<BigInteger> encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) {
        return RSAEncryption.encryptASEKey(aesKey.getEncoded(), publicKey);
    }

    // Mã hóa khóa AES bằng RSA
    public static byte[] encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey, boolean isByte) {
        return RSAEncryption.encryptASEKey(aesKey.getEncoded(), publicKey, isByte);
    }

    // Giải mã khóa AES bằng RSA
    public static SecretKey decryptAESKeyWithRSA(List<BigInteger> encryptedBlocks, BigInteger d, BigInteger n) {
        byte[] decryptedKey = RSAEncryption.decryptAESKey(encryptedBlocks, d, n);
        return new SecretKeySpec(decryptedKey, "AES");
    }

    // Giải mã tệp bằng AES và trả về mảng byte
    public static byte[] decryptFileWithAES(byte[] encryptedData, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encryptedData);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                byte[] decryptedBlock = cipher.update(buffer, 0, bytesRead);
                if (decryptedBlock != null) {
                    byteArrayOutputStream.write(decryptedBlock);
                }
            }
            byte[] finalBlock = cipher.doFinal();
            if (finalBlock != null) {
                byteArrayOutputStream.write(finalBlock);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    // Giải mã audio bằng AES và trả về mảng byte
    public static byte[] decryptAudioWithAES(byte[] encryptedBytes, SecretKey aesKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        return  cipher.update(encryptedBytes, 0, encryptedBytes.length);
    }



    public static void main(String[] args) {
        try {
            // Tạo cặp khóa RSA (công khai và riêng)
            RSAKeyPairGenerator.generateKeys();

            com.example.RSA_Algorithm.PublicKey publicKey = RSAKeyPairGenerator.publicKey;
            com.example.RSA_Algorithm.PrivateKey privateKey = RSAKeyPairGenerator.privateKey;

            // Tạo khóa AES và mã hóa tệp
            RSA_AES_Encryption.generateAESKey();
            System.out.println("Tệp đã được mã hóa thành công.");

            // Mã hóa khóa AES bằng RSA
            List<BigInteger> encryptedAesKey = RSA_AES_Encryption.encryptAESKeyWithRSA(RSA_AES_Encryption.aesKey, publicKey);
            System.out.println("Khóa AES đã được mã hóa: " + encryptedAesKey);

            // Giải mã khóa AES bằng RSA
            SecretKey decryptedAesKey = RSA_AES_Encryption.decryptAESKeyWithRSA(encryptedAesKey, privateKey.getD(), publicKey.getN());
            System.out.println("Khóa AES đã được giải mã.");

            System.out.println(Arrays.equals(decryptedAesKey.getEncoded(), aesKey.getEncoded()));
            while (true) {

                // Chọn tệp cần mã hóa
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(null);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File fileToEncrypt = fileChooser.getSelectedFile();
                    System.out.println("Mã hóa tệp: " + fileToEncrypt.getName());

                    // Lưu tệp đã mã hóa
                    File encryptedFile = new File("src/main/resources/Files/encrypted_" + fileToEncrypt.getName());
                    byte[] fileBytes = new byte[(int) encryptedFile.length()];
                    try (FileInputStream fis = new FileInputStream(encryptedFile)) {
                        fis.read(fileBytes);
                    }
                    RSA_AES_Encryption.encryptFileWithAES(fileToEncrypt, aesKey, "src/main/resources/Files/encrypted_" + fileToEncrypt.getName());
                    System.out.println("Tệp mã hóa đã được lưu: " + encryptedFile.getName());

                    // Giải mã tệp
                    try (FileOutputStream fos = new FileOutputStream("src/main/resources/Files/decrypted_" + fileToEncrypt.getName())) {
//                        fos.write(RSA_AES_Encryption.decryptFileWithAES(encryptedFile, aesKey)); // Đảm bảo bạn đã triển khai phương thức này
                        System.out.println("File saved as: " + "src/main/resources/Files/decrypted_" + fileToEncrypt.getName());

                        JOptionPane.showMessageDialog(null, "Lưu tệp thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ioException) {
                        System.out.println("Error saving file: " + ioException.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Có lỗi xảy ra: " + e.getMessage());
        }
    }
}


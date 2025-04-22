package com.example.RSA_Algorithm;

import javafx.util.Pair;

import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RSAEncryption {
    private static final int MAX_BLOCK_SIZE = RSAKeyPairGenerator.BIT_LENGTH * 2; // Độ dài khối (bit) : 2^u <= (n-1)

    // Phương thức mã hóa từng đoạn thông điệp bằng khóa công khai
    public static List<BigInteger> encryptMessage(String message, PublicKey publicKey) {
        BigInteger e = publicKey.getE();
        BigInteger n = publicKey.getN();
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        int blockSizeBytes = MAX_BLOCK_SIZE / 8; // Đổi từ bit sang byte
        List<BigInteger> encryptedBlocks = new ArrayList<>();

        // Chia thông điệp thành các khối nếu vượt quá độ dài 2048 bits (256 bytes)
        for (int i = 0; i < messageBytes.length; i += blockSizeBytes) {
            int end = Math.min(i + blockSizeBytes, messageBytes.length);
            byte[] block = new byte[end - i];
            System.arraycopy(messageBytes, i, block, 0, block.length);

            BigInteger messageBlock = new BigInteger(1, block);
            encryptedBlocks.add(messageBlock.modPow(e, n)); // Mã hóa từng khối
        }

        return encryptedBlocks;
    }

    public static List<BigInteger> encryptFile(File file, PublicKey publicKey) throws IOException {
        List<BigInteger> encryptedBlocks = new ArrayList<>();
        BigInteger e = publicKey.getE();
        BigInteger n = publicKey.getN();
        int blockSizeBytes = MAX_BLOCK_SIZE / 8; // Kích thước khối dữ liệu theo byte

        // Mã hóa tên tệp và thêm vào danh sách các khối mã hóa
        String fileName = file.getName();
        byte[] fileNameBytes = fileName.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < fileNameBytes.length; i += blockSizeBytes) {
            int end = Math.min(i + blockSizeBytes, fileNameBytes.length);
            byte[] block = Arrays.copyOfRange(fileNameBytes, i, end);
            BigInteger fileNameBlock = new BigInteger(1, block);
            encryptedBlocks.add(fileNameBlock.modPow(e, n));
        }

        // Mã hóa nội dung tệp
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[blockSizeBytes];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                byte[] actualBytes = Arrays.copyOf(buffer, bytesRead);
                BigInteger fileBlock = new BigInteger(1, actualBytes);
                encryptedBlocks.add(fileBlock.modPow(e, n));
            }
        }
        return encryptedBlocks;
    }

    public static List<BigInteger> encryptASEKey(byte[] keyASE, PublicKey publicKey) {
        BigInteger e = publicKey.getE();
        BigInteger n = publicKey.getN();
        int blockSizeBytes = MAX_BLOCK_SIZE / 8; // Đổi từ bit sang byte
        List<BigInteger> encryptedBlocks = new ArrayList<>();

        // Chia thông điệp thành các khối nếu vượt quá độ dài 2048 bits (256 bytes)
        for (int i = 0; i < keyASE.length; i += blockSizeBytes) {
            int end = Math.min(i + blockSizeBytes, keyASE.length);
            byte[] block = new byte[end - i];
            System.arraycopy(keyASE, i, block, 0, block.length);

            BigInteger messageBlock = new BigInteger(1, block);
            encryptedBlocks.add(messageBlock.modPow(e, n)); // Mã hóa từng khối
        }

        return encryptedBlocks;
    }

    public static byte[] encryptASEKey(byte[] keyASE, PublicKey publicKey, boolean isByte) {
        BigInteger e = publicKey.getE();
        BigInteger n = publicKey.getN();
        int blockSizeBytes = MAX_BLOCK_SIZE / 8; // Đổi từ bit sang byte
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Chia thông điệp thành các khối nếu vượt quá độ dài 2048 bits (256 bytes)
        for (int i = 0; i < keyASE.length; i += blockSizeBytes) {
            int end = Math.min(i + blockSizeBytes, keyASE.length);
            byte[] block = new byte[end - i];
            System.arraycopy(keyASE, i, block, 0, block.length);

            BigInteger messageBlock = new BigInteger(1, block);
            BigInteger encryptedBlock = messageBlock.modPow(e, n); // Mã hóa từng khối

            // Thêm các byte đã mã hóa vào byteArrayOutputStream
            byte[] encryptedBytes = encryptedBlock.toByteArray();
            byteArrayOutputStream.write(encryptedBytes, 0, encryptedBytes.length);
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Giải mã danh sách các khối BigInteger đã mã hóa bằng cách sử dụng các thành phần khóa riêng (d và n),
     * và trả về thông điệp ban đầu dưới dạng chuỗi UTF-8.
     * <p>
     * Phương thức này được sử dụng trong ngữ cảnh giải mã RSA, trong đó danh sách đầu vào chứa các khối dữ liệu
     * đã được mã hóa bằng thuật toán RSA. Quá trình giải mã liên quan đến việc chuyển đổi từng khối trở lại thành
     * dạng byte ban đầu và tái tạo thông điệp gốc từ các byte đó.
     * <p>
     * Mỗi khối được giải mã riêng lẻ bằng cách sử dụng phép lũy thừa mô-đun với mũ (d) và mô-đun (n) của khóa riêng,
     * sau đó chuyển đổi thành một mảng byte. Nếu mảng byte bắt đầu bằng byte 0, byte này sẽ được loại bỏ để đảm bảo
     * giải mã UTF-8 chính xác.
     *
     * @param encryptedBlocks danh sách các đối tượng BigInteger đại diện cho các khối dữ liệu đã mã hóa.
     * @param d               số mũ khóa riêng được sử dụng để giải mã.
     * @param n               mô-đun được sử dụng để giải mã (chung cho cả khóa công khai và khóa riêng).
     * @return thông điệp đã được giải mã dưới dạng chuỗi UTF-8.
     *
     * <p>Ghi chú: Phương thức này giả định rằng các khối dữ liệu đã mã hóa đủ nhỏ để được giải mã
     * trong phạm vi độ dài bit của mô-đun RSA. Mỗi khối nên là một đoạn mã hóa UTF-8 hợp lệ của thông điệp gốc.</p>
     *
     * <p><strong>Quá trình giải mã:</strong></p>
     * <ol>
     *   <li>Đối với mỗi khối đã mã hóa:
     *     <ul>
     *       <li>Thực hiện phép lũy thừa mô-đun với công thức: <code>decryptedBlock = encryptedBlock.modPow(d, n)</code>.</li>
     *       <li>Chuyển đổi BigInteger đã giải mã thành mảng byte.</li>
     *       <li>Nếu byte đầu tiên của mảng là 0, tạo một mảng mới loại bỏ byte đầu tiên.</li>
     *       <li>Chuyển mảng byte thành chuỗi và nối vào kết quả.</li>
     *     </ul>
     *   </li>
     * </ol>
     * @throws IllegalArgumentException nếu bất kỳ khối nào không thể giải mã đúng cách.
     */
    // Phương thức giải mã từng đoạn thông điệp bằng khóa riêng
    public static String decryptMessage(List<BigInteger> encryptedBlocks, BigInteger d, BigInteger n) {
        StringBuilder decryptedMessage = new StringBuilder();

        // Giải mã từng khối
        for (BigInteger encryptedBlock : encryptedBlocks) {
            BigInteger decryptedBlock = encryptedBlock.modPow(d, n);
            byte[] blockBytes = decryptedBlock.toByteArray();

            // Kiểm tra nếu có byte dẫn đầu là 0 và loại bỏ
            if (blockBytes.length > 1 && blockBytes[0] == 0) {
                byte[] adjustedBlock = Arrays.copyOfRange(blockBytes, 1, blockBytes.length);
                decryptedMessage.append(new String(adjustedBlock, StandardCharsets.UTF_8));
            } else {
                decryptedMessage.append(new String(blockBytes, StandardCharsets.UTF_8));
            }
        }

        return decryptedMessage.toString();
    }

    public static Pair<byte[], String> decryptFileAndGetFileName(List<BigInteger> encryptedBlocks, BigInteger d, BigInteger n) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Giải mã khối đầu tiên để lấy tên tệp
        BigInteger fileNameBlock = encryptedBlocks.get(0).modPow(d, n);
        byte[] fileNameBytes = fileNameBlock.toByteArray();

        // Loại bỏ byte dẫn đầu nếu cần để đảm bảo chuỗi UTF-8 chính xác
        if (fileNameBytes.length > 1 && fileNameBytes[0] == 0) {
            fileNameBytes = Arrays.copyOfRange(fileNameBytes, 1, fileNameBytes.length);
        }
        String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);

        // Bỏ qua khối đầu tiên vì đã được sử dụng để lấy tên tệp
        for (int i = 1; i < encryptedBlocks.size(); i++) {
            BigInteger decryptedBlock = encryptedBlocks.get(i).modPow(d, n);
            byte[] blockBytes = decryptedBlock.toByteArray();

            // Loại bỏ byte dẫn đầu nếu cần để đảm bảo không gây lỗi
            if (blockBytes.length > 1 && blockBytes[0] == 0) {
                blockBytes = Arrays.copyOfRange(blockBytes, 1, blockBytes.length);
            }

            byteArrayOutputStream.write(blockBytes);
        }

        byte[] fileContent = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();

        // Trả về pair chứa nội dung file và tên file
        return new Pair<>(fileContent, fileName);
    }

    public static byte[] decryptAESKey(List<BigInteger> encryptedBlocks, BigInteger d, BigInteger n) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Giải mã từng khối
        for (BigInteger encryptedBlock : encryptedBlocks) {
            BigInteger decryptedBlock = encryptedBlock.modPow(d, n);
            byte[] blockBytes = decryptedBlock.toByteArray();

            // Kiểm tra nếu có byte dẫn đầu là 0 và loại bỏ
            if (blockBytes.length > 1 && blockBytes[0] == 0) {
                blockBytes = Arrays.copyOfRange(blockBytes, 1, blockBytes.length);
            }

            try {
                byteArrayOutputStream.write(blockBytes);
            } catch (IOException e) {
                System.err.println("Error writing to ByteArrayOutputStream: " + e.getMessage());
            }
        }

        return byteArrayOutputStream.toByteArray();
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
//        // Tạo cặp khóa RSA mới
        RSAKeyPairGenerator.generateKeys();
        String message = """
                Ngôi nhà mơ ước của tôi sẽ nằm ở một vùng nông thôn yên bình, bao quanh bởi thiên nhiên. Tôi hình dung đó là một căn nhà gỗ ấm cúng với những cửa sổ lớn để đón nhiều ánh sáng mặt trời và ngắm cảnh đồng xanh và cây cối. Ngôi nhà sẽ có một phòng khách rộng rãi với lò sưởi, một phòng ngủ thoải mái, và một căn bếp nhỏ.
                
                Điều làm cho ngôi nhà trở nên đặc biệt là hệ thống nhà thông minh tiên tiến. Mọi thứ từ ánh sáng, nhiệt độ đến an ninh đều sẽ được điều khiển tự động, mang lại sự tiện lợi và tiết kiệm năng lượng. Bên ngoài sẽ có một sân nhỏ và một khu vườn với hoa, một mảnh vườn trồng rau và vài cây ăn quả. Tôi muốn có một hiên nhỏ để ngồi thư giãn và lắng nghe tiếng chim hót. Đối với tôi, ngôi nhà này sẽ là sự kết hợp hoàn hảo giữa thiên nhiên và công nghệ hiện đại, là nơi lý tưởng để tránh xa thành phố.
                """;
//        // Lưu khóa vào tệp
//        RSAKeyStorage.savePublicKey(RSAKeyPairGenerator.publicKey, "publicKey.dat");
//        RSAKeyStorage.savePrivateKey(RSAKeyPairGenerator.privateKey, "privateKey.dat");

//        // Đọc khóa từ tệp
//        PublicKey publicKey = RSAKeyStorage.loadPublicKey("publicKey.dat");
//        PrivateKey privateKey = RSAKeyStorage.loadPrivateKey("privateKey.dat");

//        System.out.println(publicKey.getN());
//        System.out.println(publicKey.getE());
//        System.out.println("-----------------------------------");
//        System.out.println(privateKey.getD());
//        System.out.println(privateKey.getP());
//        System.out.println(privateKey.getQ());
//
//        // Chọn tệp để mã hóa và giải mã
        while (true) {

            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                List<BigInteger> encryptedMessage = RSAEncryption.encryptFile(file, RSAKeyPairGenerator.publicKey);
                Pair<byte[], String> decryptedMessage = RSAEncryption.decryptFileAndGetFileName(encryptedMessage, RSAKeyPairGenerator.privateKey.getD(), RSAKeyPairGenerator.publicKey.getN());
                try (FileOutputStream fos = new FileOutputStream("received_" + file.getName())) {
                    fos.write(decryptedMessage.getKey());
                    System.out.println("File saved as: received_" + file.getName());
                } catch (IOException e) {
                    System.out.println("Error saving file: " + e.getMessage());
                }
            }
        }

//        List<BigInteger> encryptedMessage = RSAEncryption.encryptMessage(message, RSAKeyPairGenerator.publicKey);
//        String decryptedMessage = RSAEncryption.decryptMessage(encryptedMessage, RSAKeyPairGenerator.privateKey.getD(), RSAKeyPairGenerator.publicKey.getN());
//        System.out.println(decryptedMessage);
    }

}

package com.example.TCP_Socket;

import com.example.RSA_Algorithm.*;
import com.example.entity.User;
import com.example.gui.HomeGUI;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javafx.util.Pair;

import javax.crypto.SecretKey;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class ChatClient {
    private final String hostname;
    private final int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private HomeGUI homeGUI;
    private PublicKey receivedPublicKey;
    private User receivedUser;
    private final String IPAddress = "localhost"; // nhập IP của máy client

    public PublicKey getReceivedPublicKey() {
        return receivedPublicKey;
    }

    public ChatClient(String hostname, int port, HomeGUI homeGUI) {
        this.hostname = hostname;
        this.port = port;
        this.homeGUI = homeGUI;
    }

    public void start() {
        try {
            socket = new Socket(hostname, port);
            System.out.println("Connected to the chat server");

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            new Thread(new ReadMessages()).start();
//            new Thread(new WriteMessages()).start();

        } catch (IOException e) {
            System.out.println("Error connecting to server: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeObject(message);
            out.flush();
            System.out.println("Message sent: " + message);
        } catch (IOException e) {
            System.out.println("Error sending user: " + e.getMessage());
        }
    }

    public void sendEncryptedMessage(String message) {
        System.out.println("Message sent:\n" + message);
        List<BigInteger> encryptedMessage = RSAEncryption.encryptMessage(message, receivedPublicKey);
        try {
            EncryptedData encryptedData = new EncryptedData(encryptedMessage,"Message", receivedUser);
            out.writeObject(encryptedData);
            out.flush();
            System.out.println("Encrypted message sent:\n" + encryptedData.getData());
        } catch (IOException e) {
            System.out.println("Error sending user: " + e.getMessage());
        }
    }

    public void sendPublicKey(PublicKey publicKey) {
        try {
            out.writeObject(publicKey);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending public key: " + e.getMessage());
        }
    }

    public void sendFile(File file) {
        try {
            List<BigInteger> encryptedAesKey = RSA_AES_Encryption.encryptAESKeyWithRSA(RSA_AES_Encryption.aesKey, receivedPublicKey);
            RSA_AES_Encryption.encryptFileWithAES(file, RSA_AES_Encryption.aesKey, "src/main/resources/Files/encrypted_" + file.getName());
            System.out.println("Tệp mã hóa đã được lưu: " + "src/main/resources/Files/encrypted_" + file.getName());

            File encryptedFile = new File("src/main/resources/Files/encrypted_" + file.getName());
            byte[] fileBytes = new byte[(int) encryptedFile.length()];
            try (FileInputStream fis = new FileInputStream(encryptedFile)) {
                fis.read(fileBytes);
            }
            EncryptedData encryptedData = new EncryptedData(encryptedFile, fileBytes,"File", receivedUser, encryptedAesKey);

            out.writeObject(encryptedData);
            out.flush();
            System.out.println("Encrypted AES key sent: " + encryptedAesKey);
            System.out.println("Encrypted File sent: " + Arrays.toString(fileBytes));
        } catch (IOException e) {
            System.out.println("Error sending file: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void callUser(User caller){
        try {
            List<BigInteger> encryptedAesKey = RSA_AES_Encryption.encryptAESKeyWithRSA(RSA_AES_Encryption.aesKey, receivedPublicKey);

            EncryptedData encryptedData = new EncryptedData(caller,receivedUser,"Calling");
            encryptedData.setEncryptedAesKey(encryptedAesKey);
            encryptedData.setIPAddress(IPAddress);

            out.writeObject(encryptedData);
            out.flush();
            System.out.println("Calling " + receivedUser.getName());
        } catch (IOException e) {
            System.out.println("Error calling user: " + e.getMessage());
        }
    }

    public void acceptCall() {
        try {
            List<BigInteger> encryptedAesKey = RSA_AES_Encryption.encryptAESKeyWithRSA(RSA_AES_Encryption.aesKey, receivedPublicKey);
            EncryptedData encryptedData = new EncryptedData("AcceptCall",receivedUser);
            encryptedData.setEncryptedAesKey(encryptedAesKey);
            encryptedData.setIPAddress(IPAddress);
            out.writeObject(encryptedData);
            out.flush();
            System.out.println("Accepted");
        } catch (IOException e) {
            System.out.println("Error calling user: " + e.getMessage());
        }
    }

    public void cancelCall() {
        try {
            EncryptedData encryptedData = new EncryptedData("CancelCall",receivedUser);
            out.writeObject(encryptedData);
            out.flush();
            System.out.println("Cancelled");
        } catch (IOException e) {
            System.out.println("Error calling user: " + e.getMessage());
        }
    }
    private class ReadMessages implements Runnable {
        @Override
        public void run() {
            try {
                Object receivedObject;
                while ((receivedObject = in.readObject()) != null) {
                    if (receivedObject instanceof List<?> tempList) {
                        if (!tempList.isEmpty() && tempList.get(0) instanceof User) {
                            List<User> userList = (List<User>) tempList;
                            System.out.println("Received updated user list: " + userList.size());
                            updateUserList(userList);
                        }
                    } else if (receivedObject instanceof String message) {
                        System.out.println("Received string message: " + message);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
                        homeGUI.conversationPanel.addMessage(new FlatSVGIcon(receivedUser.getImage()), message, LocalDateTime.now().format(formatter), false);
                    } else if (receivedObject instanceof PublicKey publicKey) {
                        receivedPublicKey = publicKey;
                        System.out.println("Received public key");
                    } else if (receivedObject instanceof User user) {
                        receivedUser = user;
                        System.out.println("Received user: " + user.getId() + "-" + user.getName());
                    } else if (receivedObject instanceof EncryptedData encryptedData) {
                        System.out.println("Received string encrypted message:\n" + encryptedData.getData());
                        if (encryptedData.getDataType().equals("Message")) {
                            String decryptedMessage = RSAEncryption.decryptMessage(encryptedData.getData(), RSAKeyPairGenerator.privateKey.getD(), RSAKeyPairGenerator.publicKey.getN());
                            System.out.println("Received string decrypted message: " + decryptedMessage);
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
                            homeGUI.conversationPanel.addMessage(new FlatSVGIcon(receivedUser.getImage()), decryptedMessage, LocalDateTime.now().format(formatter), false);
                        } else if (encryptedData.getDataType().equals("File")) {
                            File encryptedFile = encryptedData.getEncryptedFile();
                            System.out.println("Received string decrypted file: " + encryptedFile.getName());
                            SecretKey decryptedAesKey = RSA_AES_Encryption.decryptAESKeyWithRSA(encryptedData.getEncryptedAesKey(), RSAKeyPairGenerator.privateKey.getD(), RSAKeyPairGenerator.publicKey.getN());
                            System.out.println("Khóa AES đã được giải mã.");
                            byte[] decryptedFile = RSA_AES_Encryption.decryptFileWithAES(encryptedData.getFileBytes(), decryptedAesKey);
                            System.out.println("Tệp đã được giải mã.");

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
                            homeGUI.conversationPanel.addMessage(new FlatSVGIcon(receivedUser.getImage()), new Pair<>(decryptedFile, encryptedFile.getName()), LocalDateTime.now().format(formatter), false);

                        } else if (encryptedData.getDataType().equals("Calling")) {
                            SecretKey decryptedAesKey = RSA_AES_Encryption.decryptAESKeyWithRSA(encryptedData.getEncryptedAesKey(), RSAKeyPairGenerator.privateKey.getD(), RSAKeyPairGenerator.publicKey.getN());
                            homeGUI.setReceivedSecretKey(decryptedAesKey);
                            homeGUI.setIPAddressCaller(encryptedData.getIPAddress());
                            homeGUI.receivedCalling();
                            System.out.println("Calling");
                        } else if (encryptedData.getDataType().equals("AcceptCall")) {
                            SecretKey decryptedAesKey = RSA_AES_Encryption.decryptAESKeyWithRSA(encryptedData.getEncryptedAesKey(), RSAKeyPairGenerator.privateKey.getD(), RSAKeyPairGenerator.publicKey.getN());
                            homeGUI.setReceivedSecretKey(decryptedAesKey);
                            homeGUI.setIPAddressReceiver(encryptedData.getIPAddress());
                            homeGUI.acceptedCall();
                            System.out.println("Accepted call");
                        }  else if (encryptedData.getDataType().equals("CancelCall")) {
                            homeGUI.canceledCall();
                            System.out.println("Canceled call");
                        }
                    }

                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Error reading from server: " + e.getMessage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Phương thức để cập nhật danh sách users và gọi trực tiếp HomeGUI để cập nhật giao diện
    public void updateUserList(List<User> newUsers) {
        // Khởi tạo và hiển thị
        homeGUI.onUserListChanged(newUsers);
    }

    public void sendUser(User user) {
        try {
            out.writeObject(user);
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending user: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
//        ChatClient client = new ChatClient("localhost", 1234);
//        client.start();
    }
}


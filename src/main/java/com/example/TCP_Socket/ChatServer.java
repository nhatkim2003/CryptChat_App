package com.example.TCP_Socket;

import com.example.RSA_Algorithm.EncryptedData;
import com.example.RSA_Algorithm.PublicKey;
import com.example.entity.User;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatServer implements Runnable {
    private static final int PORT = 1234;
    private static final Set<ClientHandler> clientHandlers = new HashSet<>();
    private static final List<User> users = new ArrayList<>();
    private ServerSocket serverSocket;
    private boolean isRunning = false;

    public ChatServer() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.out.println("Error starting server: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        isRunning = true;
        System.out.println("Chat server started on port " + PORT);
        try {
            while (isRunning) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        } finally {
            stopServer();
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            for (ClientHandler clientHandler : clientHandlers) {
                clientHandler.stop();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.out.println("Error stopping server: " + e.getMessage());
        }
    }

    public static void broadcastUserList() {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.sendUserList(new ArrayList<>(users)); // Gửi bản sao của danh sách users
        }
    }

    public static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler != sender) {
                clientHandler.sendMessage(message);
            }
        }
    }

    public static void broadcastEncryptedData(EncryptedData encryptedData) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.user.getId().equals(encryptedData.getReceivedUser().getId())) {
                clientHandler.sendEncryptedData(encryptedData);
                break;
            }
        }
    }

    public static void broadcastPublicKeyOfUser(long userID, ClientHandler receiver) {
        PublicKey publicKey = null;
        User user = null;
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getUser().getId().equals(userID)) {
                publicKey = clientHandler.getPublicKey();
                user = clientHandler.getUser();
                break;
            }
        }

        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler == receiver) {
                clientHandler.sendUser(user);
                clientHandler.sendPublicKey(publicKey);
                break;
            }
        }
    }

    public static void broadcastFile(File file, User receiver) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.user.getId().equals(receiver.getId())) {
                clientHandler.sendFile(file);
                System.out.println("Gửi file");
            }
        }
    }

    public static void removeClient(ClientHandler clientHandler, User user) {
        clientHandlers.remove(clientHandler);
        if (user != null) {
            users.remove(user);
            broadcastUserList(); // Phát danh sách cập nhật sau khi xóa user
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private User user;
        private PublicKey publicKey;

        public User getUser() {
            return user;
        }

        public PublicKey getPublicKey() {
            return publicKey;
        }

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                System.out.println("Error setting up streams for client: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            try {
                Object receivedObject;
                while ((receivedObject = in.readObject()) != null) {
                    if (receivedObject instanceof User receivedUser) {
                        this.user = receivedUser;
                        users.add(receivedUser);
                        System.out.println("User: " + receivedUser.getUsername() + ", " + receivedUser.getEmail());

                        // Phát danh sách user cập nhật
                        ChatServer.broadcastUserList();
                    } else if (receivedObject instanceof String message) {
                        if (message.contains("PublicKey")) {
                            String[] parts = message.split(" ");
                            long id = Long.parseLong(parts[1]);
                            System.out.println("Require: " + message);
                            ChatServer.broadcastPublicKeyOfUser(id, this);
                        } else {
                            System.out.println("Received message: " + message);
                            ChatServer.broadcastMessage(message, this);
                        }
                    } else if (receivedObject instanceof PublicKey receivedPublicKey) {
                        this.publicKey = receivedPublicKey;
                        System.out.println(user.getName() + " - Khoá public (" + publicKey.getE() + ", " + publicKey.getN() + ")");
                    } else if (receivedObject instanceof EncryptedData encryptedData) {
                        System.out.println("Received encrypted " + encryptedData.getDataType());
                        ChatServer.broadcastEncryptedData(encryptedData);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        public void sendUserList(List<User> userList) {
            try {
                out.writeObject(userList);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending user list: " + e.getMessage());
            }
        }

        public void sendUser(User user) {
            try {
                out.writeObject(user);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending user list: " + e.getMessage());
            }
        }

        public void sendMessage(String message) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending user list: " + e.getMessage());
            }
        }

        public void sendPublicKey(PublicKey publicKey) {
            try {
                out.writeObject(publicKey);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending user list: " + e.getMessage());
            }
        }

        public void sendEncryptedData(EncryptedData encryptedData) {
            try {
                out.writeObject(encryptedData);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending user list: " + e.getMessage());
            }
        }

        public void sendFile(File file) {
            try {
                out.writeObject(file);
                out.flush();
            } catch (IOException e) {
                System.out.println("Error sending file: " + e.getMessage());
            }
        }

        private void closeConnection() {
            try {
                ChatServer.removeClient(this, this.user);
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
                System.out.println("Connection closed: " + clientSocket);
            } catch (IOException e) {
                System.out.println("Error closing client connection: " + e.getMessage());
            }
        }

        public void stop() {
            closeConnection();
        }
    }
}

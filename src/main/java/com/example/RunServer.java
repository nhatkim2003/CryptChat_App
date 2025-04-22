package com.example;

import com.example.TCP_Socket.ChatServer;

public class RunServer {

    public static void main(String[] args) {
        // Khởi tạo và chạy server
        ChatServer chatServer = new ChatServer();
        Thread serverThread = new Thread(chatServer);
        serverThread.start();
    }
}

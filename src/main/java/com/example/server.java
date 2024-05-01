package com.example;

import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class server {
    private static Map<Socket, String> clients = new HashMap<>();
    private static DBHandler dbHandler; // Add a reference to DBHandler

    public static void main(String[] args) {
        dbHandler = new DBHandler(); // Initialize DBHandler
        dbHandler.connect(); // Connect to the database

        try {
            ServerSocket ss = new ServerSocket(9999);
            System.out.println("Party is created!!!");
            while (true) {
                Socket socket = ss.accept();
                System.out.println("Client connected: " + socket);
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private DataInputStream comingMsg;
        private DataOutputStream goingMsg;
        private String nickname;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                comingMsg = new DataInputStream(socket.getInputStream());
                goingMsg = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                goingMsg.writeUTF("Enter your nickname: ");
                nickname = comingMsg.readUTF();
                clients.put(socket, nickname);

                broadcastMessage(nickname + " has joined the chat.");

                while (true) {
                    String msg = comingMsg.readUTF();
                    broadcastMessage(nickname + ": " + msg);

                    try {
                        dbHandler.addMsgs(nickname, msg); // Use DBHandler to add messages to the database
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.out.println("Error While Inserting the data to the DB");
                    }

                    if (msg.equalsIgnoreCase("exit")) {
                        broadcastMessage(nickname + " has left the chat.");
                        closeConnection();
                        break;
                    }
                }
            } catch (IOException e) {
                broadcastMessage(nickname + " has left the chat.");
            }
        }

        private void broadcastMessage(String message) {
            for (Socket clientSocket : clients.keySet()) {
                try {
                    DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
                    clientOut.writeUTF(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void closeConnection() {
            try {
                clients.remove(socket);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class DBHandler {
        private Connection db;

        public void connect() {
            try {
                db = DriverManager.getConnection(
                        "jdbc:ucanaccess://C:/Users/Haider Ahmad/OneDrive/Desktop/New folder/whatsappclonesomewhat/Chats.accdb");
                System.out.println("Connected to database!!!");
            } catch (SQLException e) {
                System.out.println("Couldnt connect");
                System.out.println(e.getMessage());
            }
        }

        public void addMsgs(String nickname, String msg) throws SQLException {
            String sql = "INSERT INTO chatdetails (client_name, message) VALUES (?, ?)";
            PreparedStatement pstmt = db.prepareStatement(sql);
            pstmt.setString(1, nickname);
            pstmt.setString(2, msg);
            pstmt.executeUpdate();
            pstmt.close();
        }
    }
}

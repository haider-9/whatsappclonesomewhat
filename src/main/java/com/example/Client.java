package com.example;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private DataInputStream incomingMsg;
    private DataOutputStream outgoingMsg;

    public Client(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            incomingMsg = new DataInputStream(socket.getInputStream());
            outgoingMsg = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            outgoingMsg.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startListening(WhatsaPP gui) {
        Thread receivingThread = new Thread(() -> {
            try {
                while (true) {
                    String msg = incomingMsg.readUTF();
                    gui.appendToChatArea(msg, java.awt.Color.BLACK);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receivingThread.start();
    }
}

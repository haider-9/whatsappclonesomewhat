package com.example;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class WhatsaPP extends JFrame {
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private Client client;

    public WhatsaPP() {
        initComponents();
        client = new Client("localhost", 9999);
        client.startListening(this);
    }

    private void initComponents() {
        // Set custom colors
        Color primaryColor = new Color(51, 153, 255);
        Color secondaryColor = new Color(255, 255, 255);
        Color buttonHoverColor = new Color(102, 178, 255);

        // Set custom fonts
        Font textFont = new Font("Arial", Font.PLAIN, 16);
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        messageField = new JTextField();
        sendButton = new JButton("Send");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        chatArea.setBackground(secondaryColor);
        chatArea.setForeground(Color.BLACK);
        chatArea.setFont(textFont);

        messageField.setBackground(secondaryColor);
        messageField.setForeground(Color.BLACK);
        messageField.setFont(textFont);

        sendButton.setBackground(primaryColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFont(buttonFont);

        // Add button hover effect
        sendButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                sendButton.setBackground(buttonHoverColor);
            }

            public void mouseExited(MouseEvent evt) {
                sendButton.setBackground(primaryColor);
            }
        });

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    private void sendButtonActionPerformed(ActionEvent evt) {
        String message = messageField.getText();
        if (!message.trim().isEmpty()) {
            client.sendMessage(message);
            messageField.setText("");
        }
    }

    public void appendToChatArea(String message, Color color) {
        MutableAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, color);

        SwingUtilities.invokeLater(() -> {
            try {
                Document doc = chatArea.getDocument();
                doc.insertString(doc.getLength(), message + "\n", attrs);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WhatsaPP().setVisible(true);
            }
        });
    }
}

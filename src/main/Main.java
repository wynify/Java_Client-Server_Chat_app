package main;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class Main {
    private static final int PORT = 5555;
    private static JTextPane chatArea;
    private static JTextField messageField;
    private static PrintWriter out;
    private static Color userColor = Color.BLACK;
    private static String username;

    public static void main(String[] args) {
    	
        JFrame frame = new JFrame("Registration Form");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        
        Image image = new ImageIcon("res/icon2.png").getImage();
        frame.setIconImage(image);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 244));
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);

        JLabel serverAddressLabel = new JLabel("Server Address:");
        JTextField serverAddressField = new JTextField("192.168.56.1", 20);

        JButton button = new JButton("Start Chat");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(100, 200, 255));
        button.setForeground(Color.WHITE);

        JButton buttonCheck = new JButton("Check Connection");
        buttonCheck.setFont(new Font("Arial", Font.BOLD, 16));
        buttonCheck.setBackground(new Color(100, 200, 255));
        buttonCheck.setForeground(Color.WHITE);

        JButton colorButton = new JButton("Choose Color");
        colorButton.setFont(new Font("Arial", Font.BOLD, 16));
        colorButton.setBackground(new Color(100, 200, 255));
        colorButton.setForeground(Color.WHITE);

        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userColor = JColorChooser.showDialog(null, "Choose your color", userColor);
                if (userColor == null) {
                    userColor = Color.BLACK;
                }
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(serverAddressLabel);
        panel.add(serverAddressField);
        panel.add(button);
        panel.add(buttonCheck);
        panel.add(colorButton);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                String server = serverAddressField.getText();

                frame.setVisible(false);

                JFrame secondFrame = new JFrame("Chat");
                secondFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                secondFrame.setSize(640, 480);
                secondFrame.setLocationRelativeTo(null);

                JPanel chatPanel = new JPanel();
                chatPanel.setLayout(new BorderLayout(10, 10));

                chatArea = new JTextPane();
                chatArea.setEditable(false);
                chatArea.setBackground(new Color(240, 240, 240));
                chatArea.setFont(new Font("Arial", Font.BOLD, 14));

                chatArea.setEditorKit(new StyledEditorKit());
                chatArea.setText("");

                JScrollPane scrollPane = new JScrollPane(chatArea);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                chatPanel.add(scrollPane, BorderLayout.CENTER);

                messageField = new JTextField();
                chatPanel.add(messageField, BorderLayout.SOUTH);

                JButton sendButton = new JButton("Send");
                sendButton.setFont(new Font("Arial", Font.BOLD, 16));
                sendButton.setBackground(new Color(100, 200, 255));
                sendButton.setForeground(Color.WHITE);

                sendButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String message = messageField.getText();
                        if (!message.isEmpty()) {
                            sendMessage(username + ": " + message, userColor);
                            messageField.setText("");
                        }
                    }
                });

                JPanel sendPanel = new JPanel();
                sendPanel.setLayout(new BorderLayout());
                sendPanel.add(messageField, BorderLayout.CENTER);
                sendPanel.add(sendButton, BorderLayout.EAST);
                chatPanel.add(sendPanel, BorderLayout.SOUTH);

                secondFrame.add(chatPanel);
                secondFrame.setVisible(true);
                
                secondFrame.setTitle("Chat - User: " + username + " | Server: " + server);

                connectToServer(server);
            }
        });

        buttonCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String server = serverAddressField.getText();
                if (checkConnection(server)) {
                    JOptionPane.showMessageDialog(frame, "Connection successful to " + server, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Connection failed to " + server, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.add(panel);
        frame.setVisible(true);
    }

    private static boolean checkConnection(String server) {
        try (Socket socket = new Socket(server, PORT)) {
            return socket.isConnected();
        } catch (IOException e) {
            return false;
        }
    }

    private static void connectToServer(String server) {
        try {
            Socket socket = new Socket(server, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        displayMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Connection failed to " + server, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void sendMessage(String message, Color color) {
        String colorHex = toHex(color);
        if (out != null) {
            out.println(message + "|" + colorHex);
        }
    }

    private static void displayMessage(String message) {
        String[] parts = message.split("\\|");
        String chatMessage = parts[0];
        String colorHex = parts.length > 1 ? parts[1] : "#000000";

        
        String[] messageParts = chatMessage.split(": ", 2);
        String username = messageParts[0];
        String userMessage = messageParts.length > 1 ? messageParts[1] : "";

        StyledDocument doc = chatArea.getStyledDocument();

        Style userStyle = doc.addStyle("UserStyle", null);
        StyleConstants.setBold(userStyle, true);
        StyleConstants.setForeground(userStyle, Color.decode(colorHex));

        Style messageStyle = doc.addStyle("MessageStyle", null);
        StyleConstants.setBold(messageStyle, false);
        StyleConstants.setForeground(messageStyle, Color.BLACK);

        try {
            doc.insertString(doc.getLength(), username + ": ", userStyle);
            doc.insertString(doc.getLength(), userMessage + "\n", messageStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}

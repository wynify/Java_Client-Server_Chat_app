package Main;

import java.awt.Color;
import java.io.*;
import java.net.Socket;
import javax.swing.*;

public class ServerConnection {
    private static final int PORT = 5555;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ChatWindow chatWindow;

    public ServerConnection(String server, ChatWindow chatWindow) {
        this.chatWindow = chatWindow;
        connectToServer(server);
    }

    public static boolean checkConnection(String server) {
        try (Socket socket = new Socket(server, PORT)) {
            return socket.isConnected();
        } catch (IOException e) {
            return false;
        }
    }

    private void connectToServer(String server) {
        try {
            socket = new Socket(server, PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String[] parts = message.split("\\|");
                        if (parts.length == 3) {
                            String chatMessage = parts[0];   // Сообщение
                            String colorHex = parts[1];      // Цвет
                            String base64Icon = parts[2];    // Иконка в Base64

                            // Если иконка пустая, ставим null
                            ImageIcon userIcon = base64Icon.isEmpty() ? null : Utils.decodeBase64ToImageIcon(base64Icon);

                            String[] messageParts = chatMessage.split(": ", 2);
                            if (messageParts.length == 2) {
                                String senderUsername = messageParts[0];
                                String messageText = messageParts[1];

                                // Преобразуем строку с цветом в объект Color
                                Color textColor = Color.decode(colorHex);

                                // Отправляем сообщение в chatWindow с правильными параметрами
                                chatWindow.displayMessage(messageText, userIcon, senderUsername, textColor);
                            }
                        }
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


    public void sendMessage(String message, Color color, String base64Icon) {
        String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        if (out != null) {
            // Если иконка пустая или null, передаем пустую строку
            String iconToSend = (base64Icon == null || base64Icon.isEmpty()) ? "" : base64Icon;
            out.println(message + "|" + colorHex + "|" + iconToSend);  // Если иконка пустая, передаем пустую строку
        }
    }

    public void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

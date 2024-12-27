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
                        // Формат сообщения: "username: message|#color"
                        String[] parts = message.split("\\|");
                        if (parts.length == 2) {
                            String chatMessage = parts[0];
                            String colorHex = parts[1];
                            String[] messageParts = chatMessage.split(": ", 2);

                            if (messageParts.length == 2) {
                                String senderUsername = messageParts[0];
                                String messageText = messageParts[1];

                                // Примерная иконка для всех пользователей
                                ImageIcon userIcon = new ImageIcon("res/default_icon.png");

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

    public void sendMessage(String message, Color color) {
        String colorHex = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        if (out != null) {
            // Преобразуем сообщение и отправляем на сервер в формате "username: message|#color"
            out.println(message + "|" + colorHex);
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

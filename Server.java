package Main;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class Server {
    private static final List<PrintWriter> clientWriters = new ArrayList<>();
    private static JTextArea consoleArea;

    private static void logToConsole(String message) {
        SwingUtilities.invokeLater(() -> {
            consoleArea.append(message + "\n");
            consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) throws Exception {
        // Console window
        JFrame consoleFrame = new JFrame("Server console");
        consoleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        consoleFrame.setSize(400, 350);
        consoleFrame.setResizable(false);
        consoleFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        consoleArea = new JTextArea();
        consoleArea.setEditable(false);
        consoleArea.setBackground(new Color(255, 255, 244));
        consoleArea.setFont(new Font("A", Font.PLAIN, 14));
        panel.add(new JScrollPane(consoleArea), BorderLayout.CENTER);

        consoleFrame.add(panel);
        consoleFrame.setVisible(true);

        // Server Side;
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        int port = 5555;
        logToConsole("Server is running");
        logToConsole("Server address: " + hostAddress);
        logToConsole("Waiting for clients to connect on port " + port + "...\n");

        ServerSocket serverSocket = new ServerSocket(port);

        try {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } finally {
            serverSocket.close();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private final PrintWriter out;
        private final BufferedReader in;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            try {
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                sendToAll("A new user has joined the chat.", "#000000", "");  // Сообщение с черным цветом и без иконки

                String message;
                while ((message = in.readLine()) != null) {
                    String[] parts = message.split("\\|");  // Разделяем сообщение, цвет и Base64 иконку
                    String chatMessage = parts[0];  // Сообщение
                    String colorHex = parts.length > 1 ? parts[1] : "#000000";  // Цвет (по умолчанию черный)
                    String base64Icon = parts.length > 2 ? parts[2] : ""; // Base64 иконка (по умолчанию пустая строка)

                    logToConsole("Received message: " + chatMessage);
                    sendToAll(chatMessage, colorHex, base64Icon);  // Передаем сообщение с цветом и иконкой всем клиентам
                }
            } catch (IOException e) {
                logToConsole("Client connection error: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    logToConsole("Error closing socket: " + e.getMessage());
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                sendToAll("A user has left the chat.", "#000000", "");  // Сообщение с черным цветом и без иконки
            }
        }

        private void sendToAll(String message, String colorHex, String base64Icon) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message + "|" + colorHex + "|" + base64Icon);  // Отправляем сообщение с цветом и иконкой
                }
            }
        }

        private void logToConsole(String message) {
            SwingUtilities.invokeLater(() -> {
                consoleArea.append(message + "\n");
                consoleArea.setCaretPosition(consoleArea.getDocument().getLength());
            });
        }
    }
}

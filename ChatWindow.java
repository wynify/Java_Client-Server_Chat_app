package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ChatWindow {
    private JFrame chatFrame;
    private JPanel chatPanel;
    private JTextField messageField;
    private String server;
    private String username;
    private Color userColor;
    private ImageIcon userIcon; 
    
    private ServerConnection connection;
    
    public static ImageIcon decodeBase64ToImageIcon(String base64String) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
            BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
            return new ImageIcon(bufferedImage);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ChatWindow(String server, String username, Color userColor, ImageIcon userIcon) {
        this.server = server;
        this.username = username;
        this.userColor = userColor;
        this.userIcon = userIcon;
        
        showChatWindow();
        connection = new ServerConnection(server, this);
    }

    public void showChatWindow() {
        chatFrame = new JFrame("Chat");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(640, 480);
        chatFrame.setLocationRelativeTo(null);
        chatFrame.setLayout(new BorderLayout());

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatFrame.add(scrollPane, BorderLayout.CENTER);

        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());
        messageField = new JTextField();
        sendPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));
        sendButton.setBackground(new Color(100, 200, 255));
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessageFromField();
            }
        });
        sendPanel.add(sendButton, BorderLayout.EAST);

        chatFrame.add(sendPanel, BorderLayout.SOUTH);

        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessageFromField();
                }
            }
        });

        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 100, 100));
        backButton.setForeground(Color.WHITE);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connection.closeConnection();
                chatFrame.dispose();  // Закрытие окна чата
                new MainMenu(); // Возврат в главное меню
            }
        });

        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.add(backButton);
        chatFrame.add(backButtonPanel, BorderLayout.NORTH);

        chatFrame.setVisible(true);
        chatFrame.setTitle("Chat - User: " + username + " | Server: " + server);
    }

    public void sendMessageFromField() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            String base64Icon = (userIcon != null) ? Utils.encodeImageToBase64(userIcon, userColor) : "";  // Пустая строка, если иконки нет
            connection.sendMessage(username + ": " + message, userColor, base64Icon);
            messageField.setText("");
        }
    }

    public void displayMessage(String message, ImageIcon userIcon, String senderUsername, Color textColor) {
        boolean isUserMessage = senderUsername.equals(username);
        MessageBox messageBox = new MessageBox(senderUsername, message, textColor, userIcon, isUserMessage);

        chatPanel.add(messageBox);  // Добавляем новый MessageBox в панель

        chatPanel.revalidate();     // Обновляем панель
        chatPanel.repaint();        // Перерисовываем панель

        JScrollPane scrollPane = (JScrollPane) chatPanel.getParent().getParent(); 
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    public void onMessageReceived(String message, String senderUsername, ImageIcon userIcon) {
        boolean isUserMessage = senderUsername.equals(username);
        displayMessage(message, userIcon, senderUsername, Color.WHITE);
    }
}

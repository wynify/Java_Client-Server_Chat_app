package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class ChatWindow {
    private JFrame chatFrame;
    private JPanel chatPanel;
    private JTextField messageField;
    private String server;
    private String username;
    private Color userColor;
    
    private ServerConnection connection;

    public ChatWindow(String server, String username, Color userColor) {
        this.server = server;
        this.username = username;
        this.userColor = userColor;
        
        showChatWindow();
        connection = new ServerConnection(server, this);
    }

    public void showChatWindow() {
        chatFrame = new JFrame("Chat");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.setSize(640, 480);
        chatFrame.setLocationRelativeTo(null);

        // Создаем основной контейнер с BorderLayout
        chatFrame.setLayout(new BorderLayout());

        // Панель для сообщений с прокруткой
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение сообщений
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatFrame.add(scrollPane, BorderLayout.CENTER);  // Добавляем в центр

        // Панель для ввода сообщений и кнопки отправки
        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        messageField = new JTextField();
        sendPanel.add(messageField, BorderLayout.CENTER);  // Добавляем поле ввода в центр

        // Кнопка отправки сообщения
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
        sendPanel.add(sendButton, BorderLayout.EAST);  // Добавляем кнопку отправки справа

        chatFrame.add(sendPanel, BorderLayout.SOUTH);  // Добавляем панель в нижнюю часть окна

        // Обработчик нажатия клавиши Enter
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessageFromField();
                }
            }
        });

        // Кнопка для выхода в главное меню
        JButton backButton = new JButton("Back to Main Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(255, 100, 100));
        backButton.setForeground(Color.WHITE);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connection.closeConnection();  // Закрыть соединение при выходе
                chatFrame.setVisible(false); // Скрыть окно чата
                new MainMenu(); // Показать главное меню
            }
        });

        // Добавляем кнопку на панель (вверх)
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        backButtonPanel.add(backButton);
        chatFrame.add(backButtonPanel, BorderLayout.NORTH); // Размещение кнопки в верхней части

        // Показать чат
        chatFrame.setVisible(true);
        chatFrame.setTitle("Chat - User: " + username + " | Server: " + server);
    }

    private void sendMessageFromField() {
        String message = messageField.getText();
        if (!message.isEmpty()) {
            connection.sendMessage(username + ": " + message, userColor);
            messageField.setText(""); // Очистить поле после отправки
        }
    }

    public void displayMessage(String message, ImageIcon userIcon, String senderUsername, Color textColor) {
        // Проверяем, является ли это сообщение от текущего пользователя
        boolean isUserMessage = senderUsername.equals(username);

        // Создание нового MessageBox с полученным сообщением
        MessageBox messageBox = new MessageBox(senderUsername, message, textColor, userIcon, isUserMessage);
        
        // Добавляем новый MessageBox в панель
        chatPanel.add(messageBox);  // Добавляем в панель сообщений

        // Обновляем вид панели
        chatPanel.revalidate();     // Перерисовываем панель
        chatPanel.repaint();        // Обновляем отображение

        // Прокрутка вниз после добавления нового сообщения
        JScrollPane scrollPane = (JScrollPane) chatPanel.getParent().getParent(); // Получаем JScrollPane через два уровня родителя
        JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    
    public void onMessageReceived(String message, String senderUsername, ImageIcon userIcon) {
        // Проверяем, кто отправил сообщение
        boolean isUserMessage = senderUsername.equals(username);  // Если отправитель совпадает с нашим username, это наше сообщение
        displayMessage(message, userIcon, senderUsername, Color.WHITE);  // Отправляем сообщение в displayMessage
    }

}

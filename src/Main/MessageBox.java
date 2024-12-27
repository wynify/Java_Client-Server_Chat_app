package Main;

import javax.swing.*;
import java.awt.*;

public class MessageBox extends JPanel {
    public MessageBox(String username, String message, Color textColor, ImageIcon userIcon, boolean isUserMessage) {
        // Основной контейнер для компонента, BoxLayout позволяет элементам располагаться по вертикали
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        // Создаем верхнюю панель для иконки и имени пользователя
        JPanel topPanel = new JPanel();
        // Переключаем выравнивание в зависимости от того, от кого сообщение
        topPanel.setLayout(new FlowLayout(isUserMessage ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));  
        topPanel.setOpaque(false);

        // Иконка пользователя
        JLabel iconLabel = new JLabel(userIcon);
        iconLabel.setPreferredSize(new Dimension(30, 30));  // Размер иконки
        topPanel.add(iconLabel);
        
        // Имя пользователя
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        usernameLabel.setForeground(textColor);
        topPanel.add(usernameLabel);

        add(topPanel);  // Добавляем верхнюю панель (иконка + имя)

        // Используем JTextArea для вывода сообщения с переносами
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);  // Переносить слова
        messageArea.setLineWrap(true);      // Включить перенос строк
        messageArea.setBackground(Color.WHITE);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Устанавливаем выравнивание текста сообщения
        messageArea.setAlignmentX(isUserMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);  

        // Убираем выделение текста
        messageArea.setCaretPosition(0);

        // Граница для выделения
        messageArea.setBorder(BorderFactory.createLineBorder(textColor));

        // Оборачиваем JTextArea в JScrollPane для автоматического изменения высоты
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);  // Отключаем вертикальную прокрутку
        scrollPane.setMinimumSize(new Dimension(Integer.MIN_VALUE, 50));
        scrollPane.setSize(new Dimension(250, 50)); // Устанавливаем предпочтительную высоту для начальной загрузки
        scrollPane.setMaximumSize(new Dimension(250, Integer.MAX_VALUE)); // Устанавливаем максимальную высоту
        
        // Выравнивание самого блока с сообщением
        scrollPane.setAlignmentX(isUserMessage ? Component.LEFT_ALIGNMENT : Component.RIGHT_ALIGNMENT);  

        add(scrollPane);  // Добавляем JScrollPane с сообщением

        setOpaque(false);  // Панель не будет иметь фона
    }
}

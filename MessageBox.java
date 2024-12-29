package Main;

import javax.swing.*;
import java.awt.*;

public class MessageBox extends JPanel {
    public MessageBox(String username, String message, Color textColor, ImageIcon userIcon, boolean isUserMessage) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(isUserMessage ? FlowLayout.RIGHT : FlowLayout.LEFT, 5, 5));
        topPanel.setOpaque(false);

        // Если иконка не null, применяем изменение размера и добавляем
        JLabel iconLabel = new JLabel();
        if (userIcon != null) {
            // Уменьшаем иконку до нужного размера, например, 30x30
            userIcon = Utils.resizeIcon(userIcon, 30, 30);
            iconLabel.setIcon(userIcon);
        } else {
            // Для пустой иконки показываем квадрат с цветом
            iconLabel.setIcon(new ImageIcon(Utils.createColorSquare(textColor)));
        }
        iconLabel.setPreferredSize(new Dimension(30, 30));
        topPanel.add(iconLabel);

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 25));
        usernameLabel.setForeground(textColor);
        topPanel.add(usernameLabel);

        add(topPanel);

        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setBackground(Color.WHITE);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 12));
        messageArea.setAlignmentX(isUserMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        messageArea.setCaretPosition(0);
        messageArea.setBorder(BorderFactory.createLineBorder(textColor));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        int minHeight = Math.max(50, 30 + iconLabel.getPreferredSize().height + usernameLabel.getPreferredSize().height);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setMinimumSize(new Dimension(Integer.MIN_VALUE, minHeight));
        scrollPane.setPreferredSize(new Dimension(250, 50));
        scrollPane.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
        scrollPane.setAlignmentX(isUserMessage ? Component.LEFT_ALIGNMENT : Component.RIGHT_ALIGNMENT);

        add(scrollPane);

        setOpaque(false);
    }
}

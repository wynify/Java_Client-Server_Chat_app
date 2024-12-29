package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu {
    private JFrame mainFrame;
    private JTextField usernameField;
    private JTextField serverAddressField;
    public Color userColor = Color.BLACK;
    private String username;
    private ImageIcon userIcon;
    private String PROGRAMM_VERSION;

    public MainMenu() {
        showMainMenu();
    }

    public void showMainMenu() {
        JLabel ProgramVersion = new JLabel("Ver 0.2 α");

        mainFrame = new JFrame("Registration Form CHAT APP Ver 0.2 α");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(640, 480);
        mainFrame.setLocationRelativeTo(null);

        Image image = new ImageIcon("res/icon2.png").getImage();
        mainFrame.setIconImage(image);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 244));
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel serverAddressLabel = new JLabel("Server Address:");
        serverAddressField = new JTextField("192.168.56.1", 20);

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
        
        JButton iconButton = new JButton("Choose Icon");
        iconButton.setFont(new Font("Arial", Font.BOLD, 16));
        iconButton.setBackground(new Color(100, 200, 255));
        iconButton.setForeground(Color.WHITE);

        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userColor = JColorChooser.showDialog(null, "Choose your color", userColor);
                if (userColor == null) {
                    userColor = Color.BLACK;
                }
            }
        });

        iconButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "bmp"));
                int result = fileChooser.showOpenDialog(mainFrame);

                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    userIcon = new ImageIcon(path); // Загружаем выбранную иконку
                    iconButton.setIcon(userIcon); // Устанавливаем иконку на кнопку
                    iconButton.setText(""); // Очищаем текст на кнопке
                }
            }
        });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                username = usernameField.getText();
                String server = serverAddressField.getText();

                mainFrame.dispose(); // Закрытие главного меню
                new ChatWindow(server, username, userColor, userIcon); // Создание окна чата
            }
        });

        buttonCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String server = serverAddressField.getText();
                if (ServerConnection.checkConnection(server)) {
                    JOptionPane.showMessageDialog(mainFrame, "Connection successful to " + server, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Connection failed to " + server, "Error", JOptionPane.ERROR_MESSAGE);
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
        panel.add(iconButton);
        panel.add(ProgramVersion);
        
        mainFrame.add(panel);
        mainFrame.setVisible(true);
    }
}

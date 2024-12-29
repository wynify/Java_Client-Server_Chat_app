package Main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import javax.imageio.ImageIO;

public class Utils {

    // Метод для кодирования изображения в строку Base64
    public static String encodeImageToBase64(ImageIcon icon, Color userColor) {
        if (icon != null) {
            BufferedImage bufferedImage = toBufferedImage(icon); // Преобразуем ImageIcon в BufferedImage
            return imageToBase64(bufferedImage); // Преобразуем изображение в строку Base64
        } else {
            // Если иконка отсутствует, создаем изображение с квадратом нужного цвета
            BufferedImage colorSquare = createColorSquare(userColor);
            return imageToBase64(colorSquare); // Преобразуем цветной квадрат в строку Base64
        }
    }

    // Метод для преобразования изображения в строку Base64
    private static String imageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", byteArrayOutputStream); // Сохраняем как PNG
            byte[] imageBytes = byteArrayOutputStream.toByteArray(); // Получаем байты изображения
            return Base64.getEncoder().encodeToString(imageBytes); // Возвращаем строку Base64
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Метод для преобразования ImageIcon в BufferedImage
    public static BufferedImage toBufferedImage(ImageIcon icon) {
        java.awt.Image image = icon.getImage();
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(image, 0, 0, null);
        return bufferedImage;
    }

    // Метод для создания цветного квадрата
    public static BufferedImage createColorSquare(Color color) {
        int size = 30;  // Размер квадрата
        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, size, size);  // Рисуем квадрат с заданным цветом
        g2d.dispose();
        return bufferedImage;
    }

    public static ImageIcon decodeBase64ToImageIcon(String base64Str) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Str);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static ImageIcon resizeIcon(ImageIcon originalIcon, int width, int height) {
        // Получаем изображение из ImageIcon
        Image img = originalIcon.getImage();
        
        // Изменяем размер изображения
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        
        // Создаем новый ImageIcon с измененным размером
        return new ImageIcon(scaledImage);
    }
}

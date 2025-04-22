package com.example.gui.component;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

public class ChatMessage extends RoundedPanel {

    private final Color backgroundColor;
    private final boolean isSentByCurrentUser;

    public ChatMessage(Icon avatarIcon, String message, String timestamp, boolean isSentByCurrentUser) {
        this.isSentByCurrentUser = isSentByCurrentUser;
//        this.backgroundColor = isSentByCurrentUser ? new Color(221, 229, 250) : new Color(240, 240, 240); // Customize colors
        this.backgroundColor = isSentByCurrentUser ? new Color(221, 229, 250) : new Color(240, 240, 240); // Customize colors

        // Use FlowLayout to allow the panel to wrap around the content
        setLayout(new FlowLayout(isSentByCurrentUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 10));
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 255));

        // Message container to hold the avatar, message, and timestamp
        RoundedPanel containerPanel = new RoundedPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBackground(new Color(255, 255, 255, 255));

        RoundedPanel containerMessagePanel = new RoundedPanel();
        containerMessagePanel.setLayout(new BorderLayout());
        containerMessagePanel.setBackground(backgroundColor);
        containerPanel.add(containerMessagePanel, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setMaximumSize(new Dimension(300, Integer.MAX_VALUE)); // Đặt chiều rộng tối đa
        messagePanel.setOpaque(false);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 10));

        // Avatar on the left for incoming messages
        if (!isSentByCurrentUser) {
            JLabel avatarLabel = new JLabel(avatarIcon);
            messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
            avatarLabel.setPreferredSize(new Dimension(40, 40));
            avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            containerPanel.add(avatarLabel, BorderLayout.WEST);
        }

        // Message Panel

        JTextArea messageTextPane = new JTextArea();
        messageTextPane.setText(message);
        messageTextPane.setOpaque(false); // Để làm nền trong suốt nếu cần
        messageTextPane.setFont(new Font("Roboto", Font.PLAIN, 14));
        messageTextPane.setForeground(Color.BLACK);
        messageTextPane.setEditable(false); // Không cho phép chỉnh sửa
        messageTextPane.setBorder(BorderFactory.createEmptyBorder(5, 10,0, 10));

        // Thêm JTextPane vào panel
        messagePanel.add(messageTextPane);

        containerMessagePanel.add(messagePanel, BorderLayout.CENTER);

        // Add messagePanel to the containerPanel

        // Timestamp
        JLabel timestampLabel = new JLabel(timestamp);
        timestampLabel.setFont(new Font("Roboto", Font.PLAIN, 10));
        timestampLabel.setForeground(Color.GRAY);
        Border borderLayout = isSentByCurrentUser ? BorderFactory.createEmptyBorder() : BorderFactory.createEmptyBorder(0, 47, 0, 0);
        timestampLabel.setBorder(borderLayout);
        timestampLabel.setHorizontalAlignment(isSentByCurrentUser ? SwingConstants.RIGHT : SwingConstants.LEFT);
        timestampLabel.setOpaque(false);
        containerPanel.add(timestampLabel, BorderLayout.SOUTH);


        // Add the containerPanel to the main panel
        add(containerPanel);
    }

    public ChatMessage(Icon avatarIcon, Pair<byte[], String> file, String timestamp, boolean isSentByCurrentUser) {
        this.isSentByCurrentUser = isSentByCurrentUser;
        this.backgroundColor = isSentByCurrentUser ? new Color(221, 229, 250) : new Color(240, 240, 240); // Customize colors

        // Use FlowLayout to allow the panel to wrap around the content
        setLayout(new FlowLayout(isSentByCurrentUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 10, 10));
        setOpaque(false);
        setBackground(new Color(255, 255, 255, 255));
//        setBackground(new Color(201, 138, 138, 255));

        // Message container to hold the avatar, message, and timestamp
        RoundedPanel containerPanel = new RoundedPanel();
        containerPanel.setLayout(new BorderLayout());
        containerPanel.setBackground(new Color(255, 255, 255, 255));

        RoundedPanel containerMessagePanel = new RoundedPanel();
        containerMessagePanel.setLayout(new BorderLayout());
        containerMessagePanel.setBackground(backgroundColor);
        containerPanel.add(containerMessagePanel, BorderLayout.CENTER);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setOpaque(false);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 10));

        // Avatar on the left for incoming messages
        if (!isSentByCurrentUser) {
            JLabel avatarLabel = new JLabel(avatarIcon);
            messagePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));
            avatarLabel.setPreferredSize(new Dimension(40, 40));
            avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            containerPanel.add(avatarLabel, BorderLayout.WEST);
        }

        // Message Panel
        JLabel iconFile = new JLabel(new FlatSVGIcon("icon/file.svg"));
        iconFile.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        iconFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (!isSentByCurrentUser) {
            iconFile.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    // Create a JFileChooser instance pointing to the user's home directory
                    JFileChooser fileChooser = new JFileChooser("src/main/resources/Files/");

                    // Set the selection mode to directories only
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                    // Show the save dialog
                    int returnValue = fileChooser.showSaveDialog(null);

                    // Check if the user selected a directory
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File selectedDirectory = fileChooser.getSelectedFile();
                        String path = selectedDirectory.getAbsolutePath();
                        // Đảm bảo rằng đường dẫn kết thúc bằng dấu phân cách
                        if (!path.endsWith(File.separator)) {
                            path += File.separator;
                        }

                        try (FileOutputStream fos = new FileOutputStream(path + "decrypted_" + file.getValue())) {
                            fos.write(file.getKey()); // Đảm bảo bạn đã triển khai phương thức này
                            System.out.println("File saved as: " + path + "receivedFile" + file.getValue());

                            JOptionPane.showMessageDialog(null, "Lưu tệp thành công.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        } catch (IOException ioException) {
                            System.out.println("Error saving file: " + ioException.getMessage());
                        }
                    }
                }
            });
        }
        containerMessagePanel.add(iconFile, BorderLayout.WEST);

        JTextField messageLabel = new JTextField(file.getValue());
        messageLabel.setOpaque(false);
        messageLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messageLabel.setEditable(false);
        containerMessagePanel.add(messagePanel, BorderLayout.CENTER);

        // Add messagePanel to the containerPanel

        // Timestamp
        JLabel timestampLabel = new JLabel(timestamp);
        timestampLabel.setFont(new Font("Roboto", Font.PLAIN, 10));
        timestampLabel.setForeground(Color.GRAY);
        Border borderLayout = isSentByCurrentUser ? BorderFactory.createEmptyBorder() : BorderFactory.createEmptyBorder(0, 47, 0, 0);
        timestampLabel.setBorder(borderLayout);
        timestampLabel.setHorizontalAlignment(isSentByCurrentUser ? SwingConstants.RIGHT : SwingConstants.LEFT);
        timestampLabel.setOpaque(false);
        containerPanel.add(timestampLabel, BorderLayout.SOUTH);


        // Add the containerPanel to the main panel
        add(containerPanel);
    }

    private void saveFile(File file) {

    }

    private byte[] fileToBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            return baos.toByteArray();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        // Calculate preferred size based on content
        int width = Math.min(250, getMessageWidth()); // Limit width to 250 pixels
        int height = getMessageHeight();
        return new Dimension(width, height);
    }

    // Helper methods to calculate width and height based on content
    private int getMessageWidth() {
        return getFontMetrics(new Font("Segoe UI", Font.PLAIN, 14)).stringWidth("<html>" + "Example message" + "</html>") + 20; // Additional padding
    }

    private int getMessageHeight() {
        int lineHeight = getFontMetrics(new Font("Segoe UI", Font.PLAIN, 14)).getHeight();
        int messageLines = 2; // Example of 2 lines
        return lineHeight * messageLines + 20; // Additional padding
    }
}

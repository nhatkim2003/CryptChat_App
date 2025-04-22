package com.example.gui.component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ChatBubble extends JPanel {

    public boolean isActive = false;
    private Color backgroundColor = new Color(255, 255, 255);
    private final Color hoverColor = new Color(225, 224, 224);

    public ChatBubble(Icon avatarIcon, String name, String time, String message) {
        setLayout(new BorderLayout());
        setOpaque(false);

        JLabel avatarLabel = new JLabel(avatarIcon);
        avatarLabel.setPreferredSize(new Dimension(50, 50));
        avatarLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false); // Transparent background
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Roboto", Font.BOLD, 15));
        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Roboto", Font.PLAIN, 15));
        timeLabel.setForeground(Color.DARK_GRAY);

        headerPanel.add(nameLabel, BorderLayout.WEST);
        headerPanel.add(timeLabel, BorderLayout.EAST);

        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setFont(new Font("Roboto", Font.PLAIN, 15));
        messageLabel.setForeground(Color.BLACK);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(headerPanel, BorderLayout.NORTH);
        textPanel.add(messageLabel, BorderLayout.CENTER);
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(avatarLabel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);

        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    backgroundColor = hoverColor;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    backgroundColor = new Color(255, 255, 255);
                    repaint();
                }
            }
        });

    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);

        int arcSize = 20;
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), arcSize, arcSize));
        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 80);
    }

    // Method to set the background color of ChatBubble
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint(); // Repaint to apply the new background color
    }

//    public static void main(String[] args) {
//        JFrame frame = new JFrame("ChatBubble Example");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 150);
//
//        ChatBubble chatBubble = new ChatBubble(new FlatSVGIcon("icon/avatar.svg"), "Steve", "Tue, 08:10 AM", "Yes, we had fun.");
//
//        frame.setLayout(new FlowLayout());
//        frame.add(chatBubble);
//        frame.setVisible(true);
//    }
}


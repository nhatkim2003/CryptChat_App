package com.example.gui.component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ContactBar extends JPanel {

    public ContactBar(String contactName, String lastSeen, ActionListener callAction) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(500, 50)); // Adjust width as needed
        setBackground(new Color(255, 255, 255));

        // Left Panel for contact name and last seen status
        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(contactName);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JLabel lastSeenLabel = new JLabel(lastSeen);
        lastSeenLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lastSeenLabel.setForeground(Color.GRAY);

        namePanel.add(nameLabel);
        namePanel.add(lastSeenLabel);

        // Right Panel for action icons
        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        iconPanel.setOpaque(false);

        // Add icons (replace with actual icon paths if needed)
//        iconPanel.add(new JLabel(new FlatSVGIcon("icon/video.svg"))); // Video call icon
        JLabel iconPhone = new JLabel(new FlatSVGIcon("icon/phone.svg"));
        iconPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        iconPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Thực hiện hành động khi nhấn vào biểu tượng điện thoại
                if (callAction != null) {
                    callAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "call"));
                }
            }
        });
        iconPanel.add(iconPhone); // Phone call icon

        iconPanel.add(new JLabel(new FlatSVGIcon("icon/search.svg"))); // Search icon
        iconPanel.add(new JLabel(new FlatSVGIcon("icon/menu.svg"))); // More options icon

        // Add the panels to the ContactBar
        add(namePanel, BorderLayout.WEST);
        add(iconPanel, BorderLayout.EAST);

        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 60); // Adjust width and height as necessary
    }
}

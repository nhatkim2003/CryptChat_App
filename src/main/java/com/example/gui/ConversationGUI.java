package com.example.gui;

import com.example.gui.component.ChatMessage;
import javafx.util.Pair;
import net.miginfocom.swing.MigLayout;
import raven.scroll.win11.ScrollPaneWin11;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class ConversationGUI extends JPanel {

    private final JPanel messagesPanel;
    private final ScrollPaneWin11 scrollPane;

    public ConversationGUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));
        setBorder(new MatteBorder(1, 0, 2, 0, new Color(240, 240, 240)));

        messagesPanel = new JPanel();
        messagesPanel.setLayout(new MigLayout("", "5[fill, grow]15", "0[]0"));
        messagesPanel.setBackground(new Color(255, 255, 255));

        scrollPane = new ScrollPaneWin11();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);


        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMessage(Icon avatarIcon, String message, String timestamp, boolean isSentByCurrentUser) {
        ChatMessage chatMessage = new ChatMessage(avatarIcon, message, timestamp, isSentByCurrentUser);
        messagesPanel.add(chatMessage, "wrap");
        messagesPanel.scrollRectToVisible(new Rectangle(0, messagesPanel.getHeight(), 1, 1));

        // Scroll to the bottom to show the latest message
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        revalidate();
        repaint();
    }

    public void addMessage(Icon avatarIcon, Pair<byte[], String> file, String timestamp, boolean isSentByCurrentUser) {
        ChatMessage chatMessage = new ChatMessage(avatarIcon, file, timestamp, isSentByCurrentUser);
        messagesPanel.add(chatMessage, "wrap");
        messagesPanel.scrollRectToVisible(new Rectangle(0, messagesPanel.getHeight(), 1, 1));

        // Scroll to the bottom to show the latest message
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = scrollPane.getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });

        revalidate();
        repaint();
    }
}

package com.example.gui.component;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import raven.scroll.win11.ScrollPaneWin11;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MessageInputPanel extends JPanel {

    private final JTextArea messageTextArea;
    private final JButton sendButton;
    private final JButton attachedButton;

    public MessageInputPanel(ActionListener sendAction, ActionListener sendFileAction) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Add padding
        setBackground(new Color(255, 255, 255));

        RoundedPanel roundedPanel = new RoundedPanel();
        roundedPanel.setBackground(new Color(240, 240, 240));
        roundedPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        roundedPanel.setLayout(new BorderLayout());
        add(roundedPanel, BorderLayout.CENTER);

        // Text area for message input
        messageTextArea = new JTextArea(2, 30); // 3 rows for a multi-line input
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setFont(new Font("Roboto", Font.PLAIN, 14));
        messageTextArea.setBackground(new Color(240, 240, 240));
        messageTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendButton.doClick(); // Simulate a click on the send button when Enter is pressed
                }
            }
        });
        ScrollPaneWin11 scrollPane = new ScrollPaneWin11();
        scrollPane.setViewportView(messageTextArea);
        scrollPane.setBackground(new Color(240, 240, 240));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        roundedPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel(new MigLayout(" aligny center", "", "[]5[]"));
        panel.setBackground(Color.white);
        add(panel, BorderLayout.EAST);

        // Send button
        sendButton = new JButton(new FlatSVGIcon("icon/sent.svg")); // Set icon size
        sendButton.setFocusable(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.setContentAreaFilled(false); // Make the button look flat
        sendButton.setBorderPainted(false); // Remove button border
        sendButton.addActionListener(sendAction); // Add action listener for sending

        // attached button
        attachedButton = new JButton(new FlatSVGIcon("icon/attach.svg")); // Set icon size
        attachedButton.setFocusable(false);
        attachedButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        attachedButton.setContentAreaFilled(false); // Make the button look flat
        attachedButton.setBorderPainted(false); // Remove button border
        attachedButton.addActionListener(sendFileAction);

        // Add text area and button to the panel
        panel.add(attachedButton);
        panel.add(sendButton);
    }

    // Method to get the message from the text area
    public String getMessageText() {
        return messageTextArea.getText().trim();
    }

    // Method to clear the message text area after sending
    public void clearMessageText() {
        messageTextArea.setText("");
    }
}

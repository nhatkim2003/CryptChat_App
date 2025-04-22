package com.example.gui.component;

import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

public class IncomingCallDialog extends JDialog {
    private JLabel label;
    private Timer timer;
    private int secondsElapsed = 0;
    private boolean isCallCanceled = false; // Biến boolean để theo dõi trạng thái chấp nhận cuộc gọi
    private boolean isCallAccepted = false; // Biến boolean để theo dõi trạng thái chấp nhận cuộc gọi

    public boolean isCallAccepted() {
        return isCallAccepted;
    }

    public void setCallAccepted(boolean callAccepted) {
        boolean oldStatus = this.isCallAccepted;
        this.isCallAccepted = callAccepted;
        firePropertyChange("callAccepted", oldStatus, isCallAccepted);

    }

    public boolean isCallCanceled() {
        return isCallCanceled;
    }

    public void setCallCanceled(boolean callAccepted) {
        boolean oldStatus = this.isCallCanceled;
        this.isCallCanceled = callAccepted;
        firePropertyChange("callCanceled", oldStatus, isCallCanceled);
    }

    public IncomingCallDialog(Frame parent, String callerName, String avatarPath) {
        super(parent, "Cuộc gọi đến", true);
        setLayout(new BorderLayout());

        // Tạo GlassPane để phủ mờ
        JComponent glassPane = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 100)); // Màu đen với độ trong suốt
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        JFrame parentFrame = (JFrame) parent;
        parentFrame.setGlassPane(glassPane);
        glassPane.setVisible(true);

        JLabel avatar = new JLabel(new FlatSVGIcon(avatarPath));
        add(avatar, BorderLayout.NORTH);

        label = new JLabel("Có cuộc gọi từ: " + callerName);
        label.setFont(new Font("Roboto", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton declineButton = new JButton("Hủy");
        declineButton.setBackground(new Color(244, 67, 54)); // Màu đỏ
        declineButton.setForeground(Color.WHITE);
        declineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setCallCanceled(true);
                stopCallTimer();
                JOptionPane.showMessageDialog(IncomingCallDialog.this, "Đã hủy cuộc gọi.");
            }
        });

        JButton acceptButton = new JButton("Chấp nhận");
        acceptButton.setBackground(new Color(126, 168, 128)); // Màu xanh lá
        acceptButton.setForeground(Color.WHITE);
        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCallTimer();
                acceptButton.setVisible(false);
                setCallAccepted(true);
            }
        });

        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 150);
        setLocationRelativeTo(parent);

        // Tắt GlassPane khi dialog đóng
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                glassPane.setVisible(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {
                glassPane.setVisible(false);
            }
        });
    }

    public void startCallTimer() {
        label.setText("Thời gian gọi: 00:00");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                secondsElapsed++;
                int minutes = secondsElapsed / 60;
                int seconds = secondsElapsed % 60;
                label.setText(String.format("Thời gian gọi: %02d:%02d", minutes, seconds));
            }
        }, 0, 1000);
    }

    public void stopCallTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public static void main(String[] args) {
//        JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 300);
//        frame.setVisible(true);
//
//        IncomingCallDialog dialog = new IncomingCallDialog(frame, "Nguyễn Hoàng Long", "avatar/avatar2.svg", null);
//        dialog.setVisible(true);
    }
}

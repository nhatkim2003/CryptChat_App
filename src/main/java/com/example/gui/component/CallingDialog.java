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

public class CallingDialog extends JDialog {
    private JLabel label;
    private Timer timer;
    private int secondsElapsed = 0;
    private boolean isCallAccepted = false; // Biến boolean để theo dõi trạng thái chấp nhận cuộc gọi
    private boolean isCallCanceled = false; // Biến boolean để theo dõi trạng thái chấp nhận cuộc gọi
    public JButton declineButton;

    public boolean isCallCanceled() {
        return isCallCanceled;
    }

    public void setCallCanceled(boolean callCanceled) {
        boolean oldStatus = this.isCallCanceled;
        this.isCallCanceled = callCanceled;
        firePropertyChange("callCanceled", oldStatus, isCallCanceled);
    }

    public void setCallAccepted(boolean callAccepted) {
        boolean oldStatus = this.isCallAccepted;
        this.isCallAccepted = callAccepted;
        firePropertyChange("callAccepted", oldStatus, isCallAccepted);
    }

    public CallingDialog(Frame parent, String calleeName, String avatarPath) {
        super(parent, "Cuộc gọi đi", true);
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

        label = new JLabel("Đang gọi đến " + calleeName);
        label.setFont(new Font("Roboto", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        declineButton = new JButton("Hủy");
        declineButton.setBackground(new Color(244, 67, 54)); // Màu đỏ
        declineButton.setForeground(Color.WHITE);
        declineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                setCallAccepted(false);
                stopCallTimer();
                JOptionPane.showMessageDialog(CallingDialog.this, "Đã hủy cuộc gọi.");
                dispose();
            }
        });
        buttonPanel.add(declineButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 150);
        setLocationRelativeTo(parent);

        // Tắt GlassPane khi dialog đóng
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                glassPane.setVisible(false); // Ẩn GlassPane khi dialog đóng
            }

            @Override
            public void windowClosed(WindowEvent e) {
                glassPane.setVisible(false); // Đảm bảo ẩn GlassPane sau khi dialog đóng
            }
        });
    }

    // Phương thức gọi để cập nhật giao diện khi người nhận chấp nhận cuộc gọi
    public void onCallAccepted() {
        label.setText("Thời gian gọi: 00:00");
        startCallTimer();
    }

    private void startCallTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (isCallAccepted) {
                    secondsElapsed++;
                    int minutes = secondsElapsed / 60;
                    int seconds = secondsElapsed % 60;
                    label.setText(String.format("Thời gian gọi: %02d:%02d", minutes, seconds));
                }
            }
        }, 0, 1000);
    }

    public void stopCallTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public boolean isCallAccepted() {
        return isCallAccepted;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setVisible(true);

        CallingDialog dialog = new CallingDialog(frame, "Nguyễn Văn A", "avatar/avatar2.svg");

        // Giả lập người nhận chấp nhận cuộc gọi sau 1 giây
        Thread thread = new Thread(() -> {
            while (true) {
                if (dialog.isCallAccepted) {
                    SwingUtilities.invokeLater(dialog::onCallAccepted);
                }
            }
        });
        thread.start();

        dialog.setVisible(true);


    }
}

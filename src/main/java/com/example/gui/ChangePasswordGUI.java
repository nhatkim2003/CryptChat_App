package com.example.gui;


import com.example.entity.User;
import com.example.service.UserService;
import com.example.utils.Email;
import com.example.utils.Password;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.springframework.context.ApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class ChangePasswordGUI extends JDialog {
    private String activeOtp;
    private User user;
    private String email;
    private final JPanel contentPane;
    private final JPanel otpEnterEmail;
    private final JPanel otpConfirmPanel;
    private final JPanel otpChangePassword;
    private final JPanel jPanelLogo;
    private final JPanel jPanelTitle;
    private final JPanel jPanelTitleLogin;
    private final JPanel formChangePassword;
    private JLabel labelLogo;
    private final JLabel labelChangePassword;
    private int step;
    private final long seconds = 180;
    private Thread currentCountDownThread;
    private final UserService userService;
    private final LoginGUI loginGUI;
    private final ApplicationContext applicationContext;

    public ChangePasswordGUI(LoginGUI loginGUI, ApplicationContext applicationContext) {
        super((Frame) null, "Change Password", true);

        this.loginGUI = loginGUI;
        this.applicationContext = applicationContext;

        userService = this.applicationContext.getBean(UserService.class);

        otpEnterEmail = new JPanel(new MigLayout("", "[]", "[]0[]"));
        otpEnterEmail.setPreferredSize(new Dimension(250, 400));
        otpEnterEmail.setBackground(new Color(255, 255, 255));

        otpConfirmPanel = new JPanel(new MigLayout("", "[]", "[]0[]"));
        otpConfirmPanel.setPreferredSize(new Dimension(250, 400));
        otpConfirmPanel.setBackground(new Color(255, 255, 255));


        otpChangePassword = new JPanel(new MigLayout("", "[]", "[]0[]"));
        otpChangePassword.setPreferredSize(new Dimension(250, 400));
        otpChangePassword.setBackground(new Color(255, 255, 255));

        user = new User();
        email = "";

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(255, 255, 255));
        setContentPane(contentPane);

        formChangePassword = new JPanel(new FlowLayout());
        formChangePassword.setBackground(new Color(255, 255, 255));
        formChangePassword.setPreferredSize(new Dimension(400, 500));
        contentPane.add(formChangePassword, BorderLayout.EAST);

        jPanelLogo = new JPanel(new BorderLayout());
        jPanelLogo.setBackground(new Color(255, 255, 255));
        jPanelLogo.setPreferredSize(new Dimension(300, 500));
        contentPane.add(jPanelLogo, BorderLayout.WEST);

        labelLogo = new JLabel();
        labelLogo.setHorizontalAlignment(SwingConstants.CENTER);

        labelLogo = new JLabel(new FlatSVGIcon("image/cryptTalk_logo.svg"));
        jPanelLogo.add(labelLogo);

        jPanelTitle = new JPanel();
        jPanelTitle.setBackground(new Color(255, 255, 255));

        jPanelTitleLogin = new JPanel(new GridBagLayout());
        jPanelTitleLogin.setBackground(new Color(255, 255, 255));
        jPanelTitleLogin.setPreferredSize(new Dimension(300, 100));
        jPanelTitle.add(jPanelTitleLogin);

        labelChangePassword = new JLabel("Change Password", SwingConstants.CENTER);
        labelChangePassword.setFont(new Font("Roboto", Font.BOLD, 30));
        jPanelTitleLogin.add(labelChangePassword);

        toStep(step = 1);
        setSize(750, 500);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        try {
            BufferedImage image = ImageIO.read(new File("src/main/resources/image/cryptTalk.png"));
            setIconImage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setLocationRelativeTo(loginGUI);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
            }
        });
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void showEnterEmail() {
        otpEnterEmail.removeAll();

        JLabel lbEnterEmail = new JLabel("Input your email", SwingConstants.CENTER);
        lbEnterEmail.setFont(new Font("Roboto", Font.BOLD, 20));
        lbEnterEmail.setPreferredSize(new Dimension(500, 50));
        otpEnterEmail.add(lbEnterEmail, "span, center");

        JTextField txtEnterEmail = new JTextField();
        txtEnterEmail.setBackground(new Color(243, 246, 254));
        txtEnterEmail.setPreferredSize(new Dimension(350, 40));
        txtEnterEmail.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                    validateStep1(txtEnterEmail.getText());
            }
        });
        otpEnterEmail.add(txtEnterEmail, "span, center, wrap");

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        otpEnterEmail.add(panel, "wrap,center");

        JButton[] buttons = new JButton[2];
        buttons[0] = new JButton("Cancel");
        buttons[0].setBackground(new Color(1, 120, 220));
        buttons[0].setForeground(Color.white);
        buttons[0].setFont(new Font("Roboto", Font.BOLD, 12));
        buttons[0].addActionListener(e -> dispose());
        panel.add(buttons[0]);

        buttons[1] = new JButton("Continue");
        buttons[1].setBackground(new Color(1, 120, 220));
        buttons[1].setForeground(Color.white);
        buttons[1].setFont(new Font("Roboto", Font.BOLD, 12));
        buttons[1].addActionListener(e -> validateStep1(txtEnterEmail.getText()));
        panel.add(buttons[1]);
    }

    private void showConfirmPanel() {
        otpConfirmPanel.removeAll();

        JLabel username = new JLabel(user.getUsername(), SwingConstants.CENTER);
        username.setFont(new Font("Roboto", Font.BOLD, 20));
        username.setPreferredSize(new Dimension(500, 32));
        otpConfirmPanel.add(username, "span,center");

        JLabel label2 = new JLabel("Input OTP.", SwingConstants.CENTER);
        label2.setFont(new Font("Roboto", Font.BOLD, 14));
        label2.setPreferredSize(new Dimension(500, 32));
        otpConfirmPanel.add(label2, "span,center,wrap");

        JTextField textField = new JTextField();
        textField.setBackground(new Color(243, 246, 254));
        textField.putClientProperty("JTextField.placeholderText", "OTP");
        textField.setFont(new Font("Roboto", Font.BOLD, 20));
        textField.setPreferredSize(new Dimension(90, 40));
        textField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                String text = textField.getText();
                return text.matches("\\d{1,6}"); // Only allow 1 to 6 digits
            }
        });
        textField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
                    validateStep2(textField.getText());
            }
        });
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                currentText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

                if (currentText.matches("\\d{0,6}")) { // Only allow 0 to 6 digits
                    super.replace(fb, offset, length, text, attrs);
                    if (currentText.length() == 6)
                        validateStep2(currentText);
                }
            }
        });
        otpConfirmPanel.add(textField, "wrap,center,span");

        JLabel nothing = new JLabel();
        nothing.setPreferredSize(new Dimension(20, 10));
        otpConfirmPanel.add(nothing, "wrap,center");

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        otpConfirmPanel.add(panel, "wrap,center");

        JButton[] buttons = new JButton[3];
        buttons[0] = new JButton();
        buttons[0].setText("Gửi lại");
        buttons[0].setFont(new Font("Roboto", Font.BOLD, 12));
        buttons[0].setBackground(new Color(1, 120, 220));
        buttons[0].setForeground(Color.white);
        buttons[0].addActionListener(e -> {
            sendOTP(nothing);
        });
        panel.add(buttons[0]);

        buttons[1] = new JButton();
        buttons[1].setText("Back");
        buttons[1].setFont(new Font("Roboto", Font.BOLD, 12));
        buttons[1].setBackground(new Color(1, 120, 220));
        buttons[1].setForeground(Color.white);
        buttons[1].addActionListener(e -> toStep(--step));
        panel.add(buttons[1]);

        buttons[2] = new JButton();
        buttons[2].setText("Cancel");
        buttons[2].setFont(new Font("Roboto", Font.BOLD, 12));
        buttons[2].setBackground(new Color(1, 120, 220));
        buttons[2].setForeground(Color.white);
        buttons[2].addActionListener(e -> this.dispose());
        panel.add(buttons[2]);

        sendOTP(nothing);
    }

    private void showChangePassword() {
        JLabel title = new JLabel("Vui lòng đổi lại mật khẩu", SwingConstants.CENTER);
        title.setFont(new Font("Roboto", Font.BOLD, 20));
        title.setPreferredSize(new Dimension(500, 50));
        otpChangePassword.add(title, "span,center");

        JLabel label1 = new JLabel("Nhập mật khẩu mới", SwingConstants.CENTER);
        label1.setFont(new Font("Roboto", Font.BOLD, 14));
        label1.setPreferredSize(new Dimension(200, 32));
        otpChangePassword.add(label1, "span,center,wrap");

        JPasswordField passwordField1 = new JPasswordField();
        JPasswordField passwordField2 = new JPasswordField();

        passwordField1.setFont(new Font("Roboto", Font.PLAIN, 14));
        passwordField1.setBackground(new Color(243, 246, 254));
        passwordField1.putClientProperty("JTextField.placeholderText", "Nhập mật khẩu mới");
        passwordField1.setPreferredSize(new Dimension(200, 32));
        passwordField1.addActionListener(e -> {
            String password = new String(passwordField1.getPassword());
            String confirm = new String(passwordField2.getPassword());
            validateStep3(password, confirm);
        });
        otpChangePassword.add(passwordField1, "span,center,wrap");

        JLabel label2 = new JLabel("Nhập lại mật khẩu", SwingConstants.CENTER);
        label2.setFont(new Font("Roboto", Font.BOLD, 14));
        label2.setPreferredSize(new Dimension(200, 32));
        otpChangePassword.add(label2, "span,center,wrap");

        passwordField2.setFont(new Font("Roboto", Font.PLAIN, 14));
        passwordField2.setBackground(new Color(243, 246, 254));
        passwordField2.putClientProperty("JTextField.placeholderText", "Nhập lại mật khẩu mới");
        passwordField2.setPreferredSize(new Dimension(200, 32));
        passwordField2.addActionListener(e -> {
            String password = new String(passwordField1.getPassword());
            String confirm = new String(passwordField2.getPassword());
            validateStep3(password, confirm);
        });
        otpChangePassword.add(passwordField2, "span,center,wrap");

        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        otpChangePassword.add(panel, "span,center,wrap");

        JButton[] buttons = new JButton[2];
        buttons[0] = new JButton();
        buttons[0].setText("Xác nhận");
        buttons[0].setFont(new Font("Roboto", Font.BOLD, 12));
        buttons[0].setBackground(new Color(1, 120, 220));
        buttons[0].setForeground(Color.white);
        buttons[0].addActionListener(e -> {
            String password = new String(passwordField1.getPassword());
            String confirm = new String(passwordField2.getPassword());
            validateStep3(password, confirm);
        });
        panel.add(buttons[0]);

        buttons[1] = new JButton();
        buttons[1].setText("Hủy");
        buttons[1].setFont(new Font("Roboto", Font.BOLD, 12));
        buttons[1].setBackground(new Color(1, 120, 220));
        buttons[1].setForeground(Color.white);
        buttons[1].addActionListener(e -> this.dispose());
        panel.add(buttons[1]);
    }


    private void cancel() {
        String[] options = new String[]{"Huỷ", "Thoát"};
        int choice = JOptionPane.showOptionDialog(null, "Bạn có muốn thoát khôi phục tài khoản?",
                "Lỗi", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[1]);
        if (choice == 1)
            dispose();
    }

    public void toStep(int step) {
        JPanel panel = new JPanel();
        switch (step) {
            case 1 -> {
                showEnterEmail();
                panel = otpEnterEmail;
            }
            case 2 -> {
                showConfirmPanel();
                panel = otpConfirmPanel;
            }
            case 3 -> {
                showChangePassword();
                panel = otpChangePassword;
            }
        }
        formChangePassword.removeAll();
        formChangePassword.add(jPanelTitle, BorderLayout.NORTH);
        formChangePassword.add(panel, BorderLayout.CENTER);
        formChangePassword.repaint();
        formChangePassword.revalidate();
    }

    private void sendOTP(JLabel nothing) {
        if (currentCountDownThread != null)
            currentCountDownThread.interrupt();
        currentCountDownThread = new Thread(() -> {
            activeOtp = Email.getOTP();
            nothing.setText("Hệ thống đang gửi mã OTP...");
            Email.sendOTP(email, "Đặt lại mật khẩu", activeOtp);
            LocalTime start = LocalTime.now();
            long temp = 0;
            while (seconds - temp > 0 && !Thread.currentThread().isInterrupted()) {
                temp = LocalTime.now().until(start, ChronoUnit.SECONDS);
                nothing.setText("(" + (seconds - temp) + "s)");
            }
            activeOtp = "";
            nothing.setText("Mã OTP đã hết thời gian hiệu lực vui lòng chọn gửi lại.");
        });
        currentCountDownThread.start();
    }

    private void validateStep1(String email) {
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email của bạn.");
            return;
        }

        Optional<User> existingUser = userService.findByEmail(email);
        if (existingUser.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản.");
            return;
        }
        user = existingUser.get();
        this.email = email;
        toStep(++step);
    }

    private void validateStep2(String otp) {
        if (otp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã OTP.");
            return;
        }
        if (otp.length() != 6) {
            JOptionPane.showMessageDialog(this, "Mã OTP gồm 6 chữ số.");
            return;
        }

        if (!activeOtp.equals(otp)) {
            JOptionPane.showMessageDialog(this, "Mã OTP không đúng hoặc đã hết hạn.\nVui lòng nhập lại hoặc yêu cầu mã OTP mới");
            return;
        }
        toStep(++step);
    }

    private void validateStep3(String password, String confirm) {
        if (password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Nhập lại mật khẩu không trùng khớp với mật khẩu mới.");
            return;
        }
        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[^\\s]{3,32}$")) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không được chứa khoảng trắng.\nMật khẩu phải chứa ít nhất 1 chữ cái thường, 1 chữ cái hoa and 1 chữ số");
            return;
        }
        String hashedPassword = user.getPasswordHash();
        if (hashedPassword.startsWith("first"))
            hashedPassword = hashedPassword.substring("first".length());
        if (Password.verifyPassword(password, hashedPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải khác mật khẩu cũ.");
            return;
        }
        hashedPassword = Password.hashPassword(password);
        user.setPasswordHash("first" + hashedPassword);

        User result = userService.updateUser(user);
        if (result == null) {
            JOptionPane.showMessageDialog(this, "Thay đổi mật khẩu thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Thay đổi mật khẩu thành công");
        this.dispose();
    }
}

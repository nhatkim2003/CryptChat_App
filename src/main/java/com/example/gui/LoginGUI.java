package com.example.gui;

import com.example.RSA_Algorithm.RSAKeyPairGenerator;
import com.example.RSA_Algorithm.RSA_AES_Encryption;
import com.example.entity.User;
import com.example.gui.component.RoundedPanel;
import com.example.service.UserService;
import com.example.utils.Password;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;
import org.springframework.context.ApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class LoginGUI extends JFrame {
    private JProgressBar progressBar;
    private JPanel contentPane;
    private JPanel jPanelLogo;
    private JPanel jPanelTitle;
    private JPanel jPanelTitleLogin;
    private JPanel formLogin;
    private JPanel formInput;
    private JLabel labelLogo;
    private JLabel labelLogin;
    private JLabel labelUsername;
    private JLabel labelPassword;
    private JLabel labelForgetPasswd;
    private JTextField jTextFieldUserName;
    private JPasswordField jTextFieldPassword;
    private JButton jButtonLogin;
    private final ApplicationContext applicationContext;
    private HomeGUI homeGUI;

    public LoginGUI(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;

        SwingUtilities.invokeLater(() -> {
            try {
                // Khởi tạo HomeGUI nhưng không hiển thị
                homeGUI = new HomeGUI(applicationContext, this);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        initComponents();
//        login();
    }

    private void initComponents() {
        try {
            BufferedImage image = ImageIO.read(new File("src/main/resources/image/cryptTalk.png"));
            setIconImage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTitle("CryptTalk");
        setSize(780, 500);
        setResizable(false);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cancel();
            }
        });

        contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(255, 255, 255));
        setContentPane(contentPane);

        formLogin = new JPanel(new FlowLayout());
        formLogin.setBackground(new Color(255, 255, 255));
        formLogin.setPreferredSize(new Dimension(400, 500));
        contentPane.add(formLogin, BorderLayout.EAST);

        jPanelLogo = new JPanel(new BorderLayout());
        jPanelLogo.setBackground(new Color(255, 255, 255));
        jPanelLogo.setPreferredSize(new Dimension(380, 500));
        contentPane.add(jPanelLogo, BorderLayout.WEST);

        labelLogo = new JLabel(new FlatSVGIcon("image/cryptTalk_logo.svg"));
        labelLogo.setHorizontalAlignment(SwingConstants.CENTER);
        jPanelLogo.add(labelLogo, BorderLayout.CENTER);

        jPanelTitle = new JPanel();
        jPanelTitle.setBackground(new Color(255, 255, 255));
        formLogin.add(jPanelTitle, BorderLayout.NORTH);

        jPanelTitleLogin = new JPanel(new GridBagLayout());
        jPanelTitleLogin.setBackground(new Color(255, 255, 255));
        jPanelTitleLogin.setPreferredSize(new Dimension(300, 100));
        jPanelTitle.add(jPanelTitleLogin);

        labelLogin = new JLabel("Login", SwingConstants.CENTER);
        labelLogin.setFont(new Font("Jost", Font.BOLD, 30));
        jPanelTitleLogin.add(labelLogin);

        formInput = new JPanel(new MigLayout("", "[]", "[]0[]"));
        formInput.setBackground(new Color(255, 255, 255));
        formInput.setPreferredSize(new Dimension(350, 400));
        formLogin.add(formInput, BorderLayout.CENTER);

        labelUsername = new JLabel("Username", JLabel.LEFT);
        labelUsername.setForeground(new Color(166, 175, 182));
        labelUsername.setPreferredSize(new Dimension(100, 50));
        labelUsername.setFont(new Font("Inter", Font.BOLD, 13));
        formInput.add(labelUsername, "span, wrap");


        RoundedPanel panelUsername = new RoundedPanel();
        panelUsername.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panelUsername.setPreferredSize(new Dimension(350, 50));
        panelUsername.setBackground(new Color(221, 229, 250));
        formInput.add(panelUsername, "span, wrap");

        JLabel iconUser = new JLabel(new FlatSVGIcon("icon/user.svg"));
        panelUsername.add(iconUser);

        // sửa
        jTextFieldUserName = new JTextField();
        jTextFieldUserName.setText("longbott");
        jTextFieldUserName.setBackground(new Color(221, 229, 250));
        jTextFieldUserName.setBorder(BorderFactory.createEmptyBorder());
        jTextFieldUserName.setPreferredSize(new Dimension(270, 40));
        jTextFieldUserName.setFont(new Font("Inter", Font.BOLD, 15));
        jTextFieldUserName.putClientProperty("JTextField.placeholderText", "Enter username");
        jTextFieldUserName.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) login();
            }
        });
        panelUsername.add(jTextFieldUserName);

        labelPassword = new JLabel("Password", JLabel.LEFT);
        labelPassword.setForeground(new Color(166, 175, 182));
        labelPassword.setPreferredSize(new Dimension(100, 50));
        labelPassword.setFont(new Font("FlatLaf.style", Font.BOLD, 13));
        formInput.add(labelPassword, "span, wrap");

        RoundedPanel panelPasswd = new RoundedPanel();
        panelPasswd.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panelPasswd.setPreferredSize(new Dimension(350, 50));
        panelPasswd.setBackground(new Color(221, 229, 250));
        formInput.add(panelPasswd, "span, wrap");

        JLabel iconLock = new JLabel(new FlatSVGIcon("icon/lock.svg"));
        panelPasswd.add(iconLock);

        jTextFieldPassword = new JPasswordField();
        jTextFieldPassword.setText("Long123.");
        jTextFieldPassword.setBackground(new Color(221, 229, 250));
        jTextFieldPassword.setBorder(BorderFactory.createEmptyBorder());
        jTextFieldPassword.setPreferredSize(new Dimension(270, 40));
        jTextFieldPassword.setFont(new Font("Inter", Font.BOLD, 15));
        jTextFieldPassword.putClientProperty("JTextField.placeholderText", "Enter password");
        jTextFieldPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) login();
            }
        });
        panelPasswd.add(jTextFieldPassword);

        labelForgetPasswd = new JLabel("Forgot password?");
        labelForgetPasswd.setForeground(new Color(37, 181, 251));
        labelForgetPasswd.setFont(new Font("Inter", Font.BOLD, 13));
        labelForgetPasswd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        labelForgetPasswd.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        labelForgetPasswd.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                forgotPassword();
            }
        });
        formInput.add(labelForgetPasswd, "span, right, wrap");

        jButtonLogin = new JButton("Login");
        jButtonLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        jButtonLogin.setBackground(new Color(1, 120, 220));
        jButtonLogin.setForeground(Color.WHITE);
        jButtonLogin.setFont(new Font("Inter", Font.BOLD, 18));
        jButtonLogin.setPreferredSize(new Dimension(100, 40));
        jButtonLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                login();
            }
        });
        jButtonLogin.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) login();
            }
        });
        formInput.add(jButtonLogin, "span, wrap, center");


    }

    public void login() {
        String userName, passWord;
        userName = jTextFieldUserName.getText();
        passWord = new String(jTextFieldPassword.getPassword());
        System.out.println(userName + passWord);

        if (userName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (passWord.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu!", "L��i", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserService userService = applicationContext.getBean(UserService.class);

        Optional<User> user = userService.findByUsername(userName);

        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tài khoản không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String hashedPassword = user.get().getPasswordHash();
        if (hashedPassword.startsWith("first")) hashedPassword = hashedPassword.substring("first".length());
        if (!Password.verifyPassword(passWord, hashedPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        User account = user.get();
        showHomeGUI(account);
    }

    private void forgotPassword() {
        new ChangePasswordGUI(this, applicationContext).setVisible(true);
    }

    private void cancel() {
        String[] options = new String[]{"Thoát ứng dụng", "Huỷ"};
        int choice = JOptionPane.showOptionDialog(null, "Bạn có muốn thoát ứng dụng?", "Lỗi", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        if (choice == 0) System.exit(1);
    }

    private void showHomeGUI(User user) {
        SwingUtilities.invokeLater(() -> {
            setEnabled(true);
            dispose();
            System.gc();

            // Sinh khoá public và private
            RSAKeyPairGenerator.generateKeys();

            // Sinh khoá AES
            try {
                RSA_AES_Encryption.generateAESKey();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            homeGUI.setUser(user); // Gán user vào HomeGUI
            homeGUI.setVisible(true); // Hiển thị HomeGUI
        });
    }
}

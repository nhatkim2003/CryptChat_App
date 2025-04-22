package com.example;


import com.example.entity.User;
import com.example.gui.HomeGUI;
import com.example.gui.LoginGUI;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class CryptTalkApplication2 {
    private static LoginGUI loginGUI;
    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        FlatRobotoFont.install();
        FlatLaf.registerCustomDefaultsSource("themes");
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 13));
        FlatIntelliJLaf.setup();
        UIManager.put("ScrollBar.trackArc", 999);
        UIManager.put("ScrollBar.thumbArc", 999);
        UIManager.put("ScrollBar.trackInsets", new Insets(2, 4, 2, 4));
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        UIManager.put("ScrollBar.track", new Color(220, 221, 225, 255));
        UIManager.put("PasswordField.showRevealButton", true);
        UIManager.put("PasswordField.capsLockIcon", new FlatSVGIcon("icon/capslock.svg"));
        UIManager.put("TitlePane.iconSize", new Dimension(25, 25));
        UIManager.put("TitlePane.iconMargins", new Insets(3, 5, 0, 20));
        UIManager.put("TabbedPane.tabAreaInsets", new Insets(0, 0, 0, 0));

        applicationContext = SpringApplication.run(CryptTalkApplication2.class, args);

        // Khởi tạo và chạy server
//        ChatServer chatServer = new ChatServer();
//        Thread serverThread = new Thread(chatServer);
//        serverThread.start();


        // Khởi tạo và hiển thị LoginGUI
        SwingUtilities.invokeLater(() -> {
            try {
                // Khởi tạo LoginGUI và hiển thị nó
                loginGUI = new LoginGUI(applicationContext);
                loginGUI.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}

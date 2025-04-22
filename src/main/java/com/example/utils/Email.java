package com.example.utils;

//import com.supermarket.main.Mini_supermarketManagement;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import javax.swing.*;
import java.util.Properties;

public class Email {
    public static String getOTP() {
        int min = 100000;
        int max = 999999;
        return Integer.toString((int) ((Math.random() * (max - min)) + min));
    }

    public static void sendOTP(String toEmail, String emailSubject, String emailBody) {
        String email = "colong30082003@gmail.com";
        String password = "thplhvvpcfrtkboj";
        String host = "smtp.gmail.com";
        String port = "587";

        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(emailSubject, "utf-8");
            message.setContent(emailBody, "text/html; charset=utf-8");
            Transport.send(message);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            JOptionPane.showMessageDialog(null, "Hệ thống bị lỗi, vui lòng thử lại sau.");
        }
    }
}

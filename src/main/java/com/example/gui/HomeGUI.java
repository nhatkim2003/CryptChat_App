package com.example.gui;


import com.example.RSA_Algorithm.RSAKeyPairGenerator;
import com.example.RSA_Algorithm.RSA_AES_Encryption;
import com.example.TCP_Socket.ChatClient;
import com.example.entity.Conversation;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.gui.component.*;
import com.example.service.ConversationService;
import com.example.service.MessageService;
import com.example.service.UserService;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import javafx.util.Pair;
import net.miginfocom.swing.MigLayout;
import org.springframework.context.ApplicationContext;
import raven.scroll.win11.ScrollPaneWin11;

import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public class HomeGUI extends JFrame {

    private JPanel left;
    private JPanel right;
    private JPanel jPanelSearch;
    private RoundedPanel searchPanel;
    private JPanel title;
    private JPanel statusThumbsPanel;
    private JPanel chatListPanel;
    private ScrollPaneWin11 scrollPane;
    private ContactBar contactBar;
    private MessageInputPanel messageInputPanel;
    public ConversationGUI conversationPanel;
    private ChatBubble[] chatBubbles;
    private ChatBubble currentChat = null;
    private int currentChatBubble = -1;
    private JTextField jTextFieldSearch;
    private User user;
    private JLabel nameLabel;
    private UserService userService;
    private ConversationService conversationService;
    private MessageService messageService;

    private final ApplicationContext applicationContext;
    private ChatClient chatClient;
    private final LoginGUI loginGUI;
    private User receivedUser;
    private CallingDialog callingDialog;
    private String IPAddressCaller;
    private String IPAddressReceiver;
    private SecretKey receivedSecretKey;

    public HomeGUI(ApplicationContext applicationContext, LoginGUI loginGUI) {
        this.applicationContext = applicationContext;
        this.loginGUI = loginGUI;
        initComponents();
    }

    public void setUser(User user) {
        this.user = user;
        chatClient = new ChatClient("localhost", 1234, this);
        chatClient.start();
        chatClient.sendUser(this.user);
        chatClient.sendPublicKey(RSAKeyPairGenerator.publicKey);
        getUser();
        initChatBubbles();
    }

    public void getUser() {
        nameLabel.setText(user.getName());
    }

    public SecretKey getReceivedSecretKey() {
        return receivedSecretKey;
    }

    public void setReceivedSecretKey(SecretKey receivedSecretKey) {
        this.receivedSecretKey = receivedSecretKey;
    }

    public String getIPAddressCaller() {
        return IPAddressCaller;
    }

    public String getIPAddressReceiver() {
        return IPAddressReceiver;
    }

    public void setIPAddressReceiver(String IPAddressReceiver) {
        this.IPAddressReceiver = IPAddressReceiver;
    }

    public void setIPAddressCaller(String IPAddressCaller) {
        this.IPAddressCaller = IPAddressCaller;
    }

    public void initComponents() {
        try {
            BufferedImage image = ImageIO.read(new File("src/main/resources/image/cryptTalk.png"));
            setIconImage(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setTitle("CryptTalk");
//        setResizable(false);
        setPreferredSize(new Dimension(1248, 768));
        setMinimumSize(new Dimension());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });


        userService = applicationContext.getBean(UserService.class);
        conversationService = applicationContext.getBean(ConversationService.class);
        messageService = applicationContext.getBean(MessageService.class);

        left = new JPanel();
        right = new JPanel();
        jPanelSearch = new JPanel();
        searchPanel = new RoundedPanel();
        title = new JPanel();
        statusThumbsPanel = new JPanel();
        chatListPanel = new JPanel();
        scrollPane = new ScrollPaneWin11();
        nameLabel = new JLabel();
        jTextFieldSearch = new JTextField();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(new Color(240, 240, 240));
        setContentPane(splitPane);

        left.setLayout(new MigLayout("", "10[fill, grow]10", "10[]10"));
//        left.setBackground(new Color(191, 198, 208));
        left.setBackground(new Color(255, 255, 255));
        left.setPreferredSize(new Dimension(450, 768));
//        left.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 5, new Color(240, 240, 240)));
        splitPane.setLeftComponent(left);

        right.setLayout(new BorderLayout());
//        right.setBackground(new Color(191, 198, 208));
        right.setBackground(new Color(255, 255, 255));
//        contentPanel.add(right, BorderLayout.CENTER);
        splitPane.setRightComponent(right);

        title.setLayout(new MigLayout("insets 0, aligny center", "0[]push[]10[]0", "[]"));
        title.setBackground(new Color(255, 255, 255));
        title.setPreferredSize(new Dimension(450, 50));
        left.add(title, "wrap");

        nameLabel.setFont(new Font("SemiBold", Font.BOLD, 20));
        nameLabel.setForeground(Color.BLACK);
        title.add(nameLabel, "align left");

        JLabel searchIcon = new JLabel(new FlatSVGIcon("icon/search.svg")); // Unicode for magnifying glass
        title.add(searchIcon, "gapleft push");

        JLabel menuIcon = new JLabel(new FlatSVGIcon("icon/menu.svg")); // Unicode for menu (☰)
        title.add(menuIcon, "gapleft 10");


        jPanelSearch.setLayout(new MigLayout("", "0[]0", "0[]0"));
//        jPanelSearch.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        jPanelSearch.setBackground(new Color(255, 255, 255));
        jPanelSearch.setPreferredSize(new Dimension(450, 40));
        left.add(jPanelSearch, "wrap");

        searchPanel.setLayout(new MigLayout("", "10[]10[]10", ""));
        searchPanel.setBackground(new Color(240, 240, 240));
        jPanelSearch.add(searchPanel, "grow, push");

        jTextFieldSearch.setBackground(new Color(240, 240, 240));
        jTextFieldSearch.setPreferredSize(new Dimension(450, 30));
        jTextFieldSearch.setForeground(Color.BLACK);
        jTextFieldSearch.setBorder(BorderFactory.createEmptyBorder());
        jTextFieldSearch.putClientProperty("JTextField.placeholderText", "Search text ...");
        jTextFieldSearch.putClientProperty("JTextField.showClearButton", true);
        jTextFieldSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        searchPanel.add(jTextFieldSearch);

        statusThumbsPanel.setLayout(new MigLayout("insets 0, aligny center", "13[]10", "0[]0"));
        statusThumbsPanel.setPreferredSize(new Dimension(450, 60));
        statusThumbsPanel.setBackground(new Color(255, 255, 255));
        left.add(statusThumbsPanel, "wrap");

        JLabel allChatLabel = new JLabel("All Chats");
        allChatLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        allChatLabel.setForeground(Color.BLACK);
        allChatLabel.setIcon(new FlatSVGIcon("icon/chat-dot-square-svgrepo-com.svg"));
        left.add(allChatLabel, "span, wrap");

        chatListPanel.setLayout(new MigLayout("", "10[fill, grow]15", "0[]10"));
        chatListPanel.setBackground(new Color(255, 255, 255));

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(chatListPanel);
        scrollPane.setPreferredSize(new Dimension(450, 500));
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        left.add(scrollPane, "span, wrap");

    }

    private void sendMessage(ActionEvent e) {
        String messageText = messageInputPanel.getMessageText();
        if (!messageText.isEmpty()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
            conversationPanel.addMessage(new FlatSVGIcon("icon/avatar.svg"), messageText, LocalDateTime.now().format(formatter), true);
            chatClient.sendEncryptedMessage(messageText);
            messageInputPanel.clearMessageText();

        }
    }

    private void sendFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser("src/main/resources/Demo");
        int option = fileChooser.showOpenDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
            conversationPanel.addMessage(new FlatSVGIcon("icon/avatar.svg"), new Pair<>(null, file.getName()), LocalDateTime.now().format(formatter), true);
            chatClient.sendFile(file);
        }
    }

    private void callUser(ActionEvent actionEvent) {
        callingDialog = new CallingDialog(this, receivedUser.getName(), receivedUser.getImage());
        callingDialog.addPropertyChangeListener("callAccepted", evt -> {
            if ((boolean) evt.getNewValue()) {
                callingDialog.onCallAccepted();

//                SwingWorker<Void, Void> audioCallWorker = audioCallWorker(getIPAddressReceiver(), 50005);

                SwingWorker<Void, Void> audioReceiverWorker = audioReceiverWorker(50005);

                // Khởi chạy SwingWorker
//                audioCallWorker.execute();
                audioReceiverWorker.execute();
                callingDialog.addPropertyChangeListener("callCanceled", e -> {
                    if ((boolean) evt.getNewValue()) {
                        callingDialog.declineButton.doClick();
//                        audioCallWorker.cancel(true);
                        audioReceiverWorker.cancel(true);
                    }
                });
            }
        });

        chatClient.callUser(user);
        callingDialog.setVisible(true);
    }

    public void receivedCalling() {
        IncomingCallDialog incomingCallDialog = new IncomingCallDialog(this, receivedUser.getName(), receivedUser.getImage());
        incomingCallDialog.addPropertyChangeListener("callAccepted", evt -> {
            if ((boolean) evt.getNewValue()) {
                chatClient.acceptCall();

                SwingWorker<Void, Void> audioCallWorker = audioCallWorker(getIPAddressCaller(), 50005);

                // Khởi tạo lắng nghe audio trong luồng riêng hoặc SwingWorker
//                SwingWorker<Void, Void> audioReceiverWorker = audioReceiverWorker(50005);

                // Bắt đầu chạy SwingWorker để nhận âm thanh
                audioCallWorker.execute();
//                audioReceiverWorker.execute();
                incomingCallDialog.addPropertyChangeListener("callCanceled", e -> {
                    if ((boolean) evt.getNewValue()) {
                        chatClient.cancelCall();
                        incomingCallDialog.dispose();
                        audioCallWorker.cancel(true);
//                        audioReceiverWorker.cancel(true);
                    }
                });
            }
        });
        incomingCallDialog.setVisible(true);
    }

    public void acceptedCall() {
        callingDialog.setCallAccepted(true);
    }

    public void canceledCall() {
        callingDialog.setCallCanceled(true);
    }

    private SwingWorker<Void, Void> audioCallWorker(String IPClient, int portClient) {
        // Cài đặt cấu hình microphone và bắt đầu ghi âm
        // Địa chỉ của client nhận

        return new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // Cài đặt cấu hình microphone và bắt đầu ghi âm
                    AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
                    DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                    TargetDataLine microphone = (TargetDataLine) AudioSystem.getLine(info);
                    microphone.open(format);
                    microphone.start();

                    System.out.println("Microphone started...");
                    DatagramSocket socket = new DatagramSocket();
                    InetAddress receiverAddress = InetAddress.getByName(IPClient); // Địa chỉ của client nhận
                    int port = portClient;

                    byte[] buffer = new byte[8192];
                    while (!isCancelled()) {
                        int bytesRead = microphone.read(buffer, 0, buffer.length);
                        byte[] encryptedData = RSA_AES_Encryption.encryptAudioWithAES(buffer, bytesRead, RSA_AES_Encryption.aesKey);
                        DatagramPacket packet = new DatagramPacket(encryptedData, bytesRead, receiverAddress, port);
                        socket.send(packet);
                    }

                    microphone.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    private SwingWorker<Void, Void> audioReceiverWorker(int portListener) {

        return new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    AudioFormat format = new AudioFormat(44100, 16, 2, true, true);
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                    SourceDataLine speakers = (SourceDataLine) AudioSystem.getLine(info);
                    speakers.open(format);
                    speakers.start();

                    DatagramSocket socket = new DatagramSocket(portListener);
                    byte[] buffer = new byte[8192];

                    System.out.println("Listening for audio data...");
                    while (!isCancelled()) {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        byte[] decryptedData = RSA_AES_Encryption.decryptAudioWithAES(packet.getData(), receivedSecretKey);
                        speakers.write(decryptedData, 0, decryptedData.length);
                    }

                    speakers.drain();
                    speakers.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    // Phương thức để cập nhật giao diện khi danh sách users thay đổi
    public void onUserListChanged(List<User> users) {
        SwingUtilities.invokeLater(() -> {
            try {
                statusThumbsPanel.removeAll();

                ImageAvatar imageAvatar = new ImageAvatar();
                imageAvatar.setImage(new FlatSVGIcon(user.getImage()));
                statusThumbsPanel.add(imageAvatar);

                for (User user1 : users) {
                    if (!user1.getId().equals(user.getId())) {
                        ImageAvatar avatar = new ImageAvatar();
                        avatar.setImage(new FlatSVGIcon(user1.getImage()));
                        statusThumbsPanel.add(avatar);
                    }
                }

                statusThumbsPanel.repaint();
                statusThumbsPanel.revalidate();
                System.gc();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void initChatBubbles() {
        chatListPanel.removeAll();
        List<Conversation> conversationList = conversationService.getUserConversations(user);
        chatBubbles = new ChatBubble[conversationList.size()];
//        User firstUser = new User();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
        for (int i = 0; i < conversationList.size(); i++) {
            Conversation conversation = conversationList.get(i);
            Set<User> users = conversation.getMembers();

            Optional<User> otherUser = users.stream()
                    .filter(user -> !user.getId().equals(this.user.getId()))  // Lọc ra user khác với currentUser
                    .findFirst();

//            if (i == 0) {
//                firstUser = otherUser.get();
//            }

            List<Message> sortedMessages = messageService.getMessagesByConversation(conversation).stream()
                    .sorted(Comparator.comparing(Message::getTimestamp))  // Sắp xếp tăng dần theo thời gian
                    .toList();
            Message lastMessage = sortedMessages.get(sortedMessages.size() - 1);

            chatBubbles[i] = new ChatBubble(new FlatSVGIcon(otherUser.get().getImage()), otherUser.get().getName(), lastMessage.getTimestamp().format(formatter), lastMessage.getContent());
            int index = i;

            // Add click event for each chat bubble
            chatBubbles[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    receivedUser = otherUser.get();
                    openChat(getConversationPanel(conversationList.get(index), otherUser.get()));
                    Active(chatBubbles[index]);
                    currentChatBubble = index;
                    chatClient.sendMessage("PublicKey " + otherUser.get().getId());
                }
            });

            chatListPanel.add(chatBubbles[i], "wrap");
        }
        chatListPanel.setPreferredSize(new Dimension(400, Math.max(500, conversationList.size() * 80)));

        // Set the first chat bubble as active initially
//        chatClient.sendMessage("PublicKey " + firstUser.getId());
//        openChat(getConversationPanel(conversationList.get(0), firstUser));
//        Active(chatBubbles[0]);

    }


    public JPanel getConversationPanel(Conversation conversation, User user) {
        JPanel panel = new JPanel(new BorderLayout());

        contactBar = new ContactBar(user.getName(), "Active", this::callUser);
        panel.add(contactBar, BorderLayout.NORTH);

        conversationPanel = new ConversationGUI();

        // Sample avatar icon
        Icon avatarIcon = new FlatSVGIcon(user.getImage()); // Update with actual path

        List<Message> messageList = messageService.getMessagesByConversation(conversation).stream()
                .sorted(Comparator.comparing(Message::getTimestamp))  // Sắp xếp tăng dần theo thời gian
                .toList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

        for (Message message : messageList) {
            conversationPanel.addMessage(avatarIcon, message.getContent(), message.getTimestamp().format(formatter), message.getSender().getId().equals(this.user.getId()));
        }

        panel.add(conversationPanel, BorderLayout.CENTER);

        messageInputPanel = new MessageInputPanel(this::sendMessage, this::sendFile);
        panel.add(messageInputPanel, BorderLayout.SOUTH);
        return panel;
    }

    public void openChat(JPanel module) {
        right.removeAll();
        right.add(module, BorderLayout.CENTER);
        right.repaint();
        right.revalidate();
        System.gc();
    }

    private void Disable() {
        if (currentChat != null) {
            currentChat.setActive(false);
            currentChat.setBackgroundColor(new Color(255, 255, 255)); // Default color
        }
    }

    private void Active(ChatBubble module) {
        Disable(); // Deactivate any currently active bubble
        currentChat = module;
        module.setActive(true);
        module.setBackgroundColor(new Color(221, 229, 250)); // Highlight color
        chatListPanel.repaint();
        chatListPanel.revalidate();
    }

    public void exit() {
        int message = JOptionPane.showOptionDialog(null,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Đăng xuất", "Huỷ"},
                "Đăng xuất");
        if (message == JOptionPane.YES_OPTION) {
            dispose();
            System.gc();
            loginGUI.setVisible(true);
        }
    }

}

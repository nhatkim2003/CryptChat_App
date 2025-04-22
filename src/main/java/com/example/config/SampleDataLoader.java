package com.example.config;

import com.example.entity.Conversation;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.repository.ConversationRepository;
import com.example.repository.MessageRepository;
import com.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public SampleDataLoader(UserRepository userRepository,
                            ConversationRepository conversationRepository,
                            MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        createSampleData();
    }

    private void createSampleData() {
        // Tạo một số người dùng mẫu
        User longbott = createUserIfNotExists("longbott", "Nguyễn Hoàng Long", "avatar/avatar3.svg", "colong30082003@gmail.com", "first$2a$12$StDXLLspPPLH47JtkX6jyujf3VSvqmTEyCNsOvRr5mCUerS5/sZmC");
        User bob = createUserIfNotExists("bob", "Bob", "avatar/avatar2.svg", "bob@example.com", "first$2a$12$StDXLLspPPLH47JtkX6jyujf3VSvqmTEyCNsOvRr5mCUerS5/sZmC");
        User carol = createUserIfNotExists("carol", "Carol", "avatar/avatar1.svg", "carol@example.com", "first$2a$12$StDXLLspPPLH47JtkX6jyujf3VSvqmTEyCNsOvRr5mCUerS5/sZmC");
        User dave = createUserIfNotExists("dave", "Dave", "avatar/avatar4.svg", "dave@example.com", "first$2a$12$StDXLLspPPLH47JtkX6jyujf3VSvqmTEyCNsOvRr5mCUerS5/sZmC");
        User eve = createUserIfNotExists("eve", "Eve", "avatar/avatar5.svg", "eve@example.com", "first$2a$12$StDXLLspPPLH47JtkX6jyujf3VSvqmTEyCNsOvRr5mCUerS5/sZmC");
        User frank = createUserIfNotExists("frank", "Frank", "avatar/avatar6.svg", "frank@example.com", "first$2a$12$StDXLLspPPLH47JtkX6jyujf3VSvqmTEyCNsOvRr5mCUerS5/sZmC");

        // Tạo các cuộc hội thoại cá nhân
        Conversation conversation1 = createConversationIfNotExists("Private Chat - longbott and Bob", Set.of(longbott, bob));
        Conversation conversation2 = createConversationIfNotExists("Private Chat - Carol and Dave", Set.of(carol, dave));
        Conversation conversation3 = createConversationIfNotExists("Private Chat - longbott and Carol", Set.of(longbott, carol));
        Conversation conversation4 = createConversationIfNotExists("Private Chat - Bob and Eve", Set.of(bob, eve));
        Conversation conversation5 = createConversationIfNotExists("Private Chat - Dave and Eve", Set.of(dave, eve));
        Conversation conversation6 = createConversationIfNotExists("Private Chat - longbott and Dave", Set.of(longbott, dave));
        Conversation conversation7 = createConversationIfNotExists("Private Chat - longbott and Eve", Set.of(longbott, eve));
        Conversation conversation8 = createConversationIfNotExists("Private Chat - longbott and Frank", Set.of(longbott, frank));

        // Tạo nhiều tin nhắn mẫu cho mỗi cuộc hội thoại cá nhân
        createMessagesForConversation(conversation1, longbott, bob, 50);
        createMessagesForConversation(conversation2, carol, dave, 40);
        createMessagesForConversation(conversation3, longbott, carol, 30);
        createMessagesForConversation(conversation4, bob, eve, 20);
        createMessagesForConversation(conversation5, dave, eve, 25);
        createMessagesForConversation(conversation6, longbott, dave, 25);
        createMessagesForConversation(conversation7, longbott, eve, 25);
        createMessagesForConversation(conversation8, longbott, frank, 25);
    }

    private User createUserIfNotExists(String username, String name, String image, String email, String passwordHash) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setImage(image);
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(passwordHash);
            return userRepository.save(user);
        }
        return existingUser.get();
    }

    private Conversation createConversationIfNotExists(String name, Set<User> members) {
        Optional<Conversation> existingConversation = conversationRepository.findByName(name);
        if (existingConversation.isEmpty()) {
            Conversation conversation = new Conversation();
            conversation.setName(name);
            conversation.setMembers(members);
            return conversationRepository.save(conversation);
        }
        return existingConversation.get();
    }

    private void createMessagesForConversation(Conversation conversation, User sender1, User sender2, int messageCount) {
        LocalDateTime timestamp = LocalDateTime.now().minusMinutes(messageCount); // Lùi thời gian cho mỗi tin nhắn
        for (int i = 1; i <= messageCount; i++) {
            User sender = (i % 2 == 0) ? sender1 : sender2; // Chuyển đổi người gửi qua lại
            String content = "Message " + i + " from " + sender.getUsername();
            createMessageIfNotExists(conversation, sender, content, timestamp.plusMinutes(i));
        }
    }

    private Message createMessageIfNotExists(Conversation conversation, User sender, String content, LocalDateTime timestamp) {
        Optional<Message> existingMessage = messageRepository.findByConversationAndSenderAndContent(conversation, sender, content);
        if (existingMessage.isEmpty()) {
            Message message = new Message();
            message.setConversation(conversation);
            message.setSender(sender);
            message.setContent(content);
            message.setTimestamp(timestamp);
            return messageRepository.save(message);
        }
        return existingMessage.get();
    }
}

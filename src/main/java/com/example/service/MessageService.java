package com.example.service;

import com.example.entity.Conversation;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public Message sendMessage(Conversation conversation, User sender, String content) {
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        return messageRepository.save(message);
    }

    public List<Message> getMessagesByConversation(Conversation conversation) {
        return messageRepository.findByConversationOrderByTimestampAsc(conversation);
    }
}


package com.example.service;

import com.example.entity.Conversation;
import com.example.entity.User;
import com.example.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    public Conversation createConversation(String name) {
        Conversation conversation = new Conversation();
        conversation.setName(name);
        return conversationRepository.save(conversation);
    }

    public List<Conversation> getUserConversations(User user) {
        return conversationRepository.findByMembersContaining(user);
    }
}


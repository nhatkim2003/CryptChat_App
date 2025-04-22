package com.example.repository;

import com.example.entity.Message;
import com.example.entity.Conversation;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderByTimestampAsc(Conversation conversation);

    Optional<Message> findByConversationAndSenderAndContent(Conversation conversation, User sender, String content);
}

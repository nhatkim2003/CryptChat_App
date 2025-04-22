package com.example.repository;

import com.example.entity.Conversation;
import com.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository


public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    Optional<Conversation> findByName(String name);

    List<Conversation> findByMembersContaining(User user);
}


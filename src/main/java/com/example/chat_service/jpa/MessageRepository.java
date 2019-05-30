package com.example.chat_service.jpa;

import com.example.chat_service.vo.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Integer> {
}

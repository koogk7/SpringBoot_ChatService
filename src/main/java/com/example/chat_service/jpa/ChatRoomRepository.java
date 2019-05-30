package com.example.chat_service.jpa;

import com.example.chat_service.vo.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom,Integer> {
}

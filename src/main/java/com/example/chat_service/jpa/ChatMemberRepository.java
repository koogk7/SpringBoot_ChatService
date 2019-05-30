package com.example.chat_service.jpa;

import com.example.chat_service.vo.ChatMember;
import com.example.chat_service.vo.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember,Integer> {
    Optional<List<ChatMember>> findByMember(Member member);
    Optional<ChatMember> findByMemberAndCid(Member member, int cid);
}

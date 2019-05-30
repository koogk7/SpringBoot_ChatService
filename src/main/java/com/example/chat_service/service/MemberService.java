package com.example.chat_service.service;

import com.example.chat_service.jpa.MemberRepository;
import com.example.chat_service.vo.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service // 현재 클래스를 스프링에서 관리하는 service bean으로 등록
public class MemberService {

    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    @Autowired
    MemberRepository memberRepository;

//    @Autowired
//    S3Uploader s3Uploader;


    public boolean register(Member member){
        Optional<Member> dupliMember = memberRepository.findByEmail(member.getEmail());
        if(dupliMember.isPresent()) {
            System.out.println("중복된 회원입니다.");
            return false;
        }
        memberRepository.save(member);
        System.out.println(member.getEmail() + " 회원등록 완료");
        return true;
    }


}

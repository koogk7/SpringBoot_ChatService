package com.example.chat_service.service;


import com.example.chat_service.jpa.ChatMemberRepository;
import com.example.chat_service.jpa.ChatRoomRepository;
import com.example.chat_service.jpa.MemberRepository;
import com.example.chat_service.jpa.MessageRepository;
import com.example.chat_service.vo.ChatMember;
import com.example.chat_service.vo.ChatRoom;
import com.example.chat_service.vo.Member;
import com.example.chat_service.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class ChatService {
    private static final Logger logger =LoggerFactory.getLogger(ChatService.class);

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    ChatMemberRepository chatMemberRepo;

    @Autowired
    MessageRepository messageRepo;

    //-----------------채팅방을 생성합니다.-------------------
    public ChatRoom createRoom(String roomName, String[] memberNames){
        logger.info("Create Room Service");

        ChatRoom chatRoom = new ChatRoom(roomName);
        chatRoom = chatRoomRepository.save(chatRoom);
        saveChatMember(memberNames, chatRoom.getCid());
        makeEnterMessage(chatRoom.getCid(), memberNames);
        return chatRoom;
    }

    //-----------------채팅방에서 나갑니다.-------------------
    public boolean exitRoom(int cid, String memberName){
        logger.info("Exit Room Service");
        Optional<Member> member = memberRepository.findByName(memberName);
        if(!member.isPresent()){
            logger.error("존재하지 않는 유저입니다.");
            return false;
        }
        Optional<ChatMember> chatMember = chatMemberRepo.findByMemberAndCid(member.get(), cid);
        if(!chatMember.isPresent()){
            logger.error("채팅방 목록에 존재하지 않는 유저입니다.");
            return false;
        }
        chatMemberRepo.delete(chatMember.get());
        return true;
    }

    //-----------------채팅방 목록을 불러옵니다.-------------------
    public List<ChatRoom> loadChatList(Member member) {

        logger.info("LoadChatList");
        Optional<Member> memberO= memberRepository.findByEmail(member.getEmail());

        if(!memberO.isPresent()){
            logger.info("로그인 계정이 존재하지 않습니다");
            return null;
        }

        Optional<List<ChatMember>> chatMembers = chatMemberRepo.findByMember(memberO.get());

        if(!chatMembers.isPresent()){
            logger.info("채팅목록이 존재하지 않습니다");
            return null;
        }

        List<ChatRoom> chatRooms = new ArrayList<>();

        //채팅방목록 불러오기
        for(ChatMember chatMember: chatMembers.get()){
            Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatMember.getCid());
            if(!chatRoom.isPresent()){
                logger.info("채팅방목록에 존재하지 않는 방입니다");
                return null;
            }
            chatRooms.add(chatRoom.get());
            System.out.println("size : "+chatRoom.get().getMessages().size());
        }

        return chatRooms;
    }

    //-----------------채팅방 기록을 불러옵니다.-------------------
    public ChatRoom loadRoomInfo(int chatRoomId){
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(chatRoomId);
        if(chatRoom.isPresent()){
            return chatRoom.get();
        } else{
            logger.error("방정보가 존재하지 않습니다.");
            return null;
        }
    }

    //-----------------메시지를 저장합니다.-------------------
    public void saveMessage(Message message, String sender){
        message.setMember(this.findSender(sender)); // 조인객체는 직접 찾아줘야하나?
        messageRepo.save(message);
        logger.info("메시지 저장완료");
    }

    //-----------------발신자를 찾습니다.-------------------
    public Member findSender(String name){
        Optional<Member> member = memberRepository.findByName(name);
        if(member.isPresent()) return member.get();
        else{
            logger.error("올바른 계정이 아닙니다.");
            return null;
        }
    }

    //-----------------ChatMember를 DB에 저장합니다.-------------------
    public void saveChatMember(String[] memberNames, int cid){
        Set<String> setNames = new HashSet<String>(Arrays.asList(memberNames));
        List<Object> uidList = memberRepository.getUidByName(setNames);

        for (int i = 0; i < uidList.size(); i++) {
            ChatMember chatMember = new ChatMember();
            chatMember.setCid(cid);
            chatMember.setRole("User");
            Optional<Member> member = memberRepository.findById((int)uidList.get(i));
            if(!member.isPresent()){
                logger.error("채팅방에 없는 계정입니다.");
            }
            chatMember.setMember(member.get());
            chatMemberRepo.save(chatMember);
        }
    }

    //-----------------채팅퇴장 메시지를 만듭니다.-------------------
    public Message makeEnterMessage(int cid, String[] memberNames){

        Optional<Member> admin = memberRepository.findById(0);
        if(!admin.isPresent()){
            logger.error("관리자 계정이 존재하지 않습니다.");
            return null;
        }

        String msg = "";

        for (int i = 0; i < memberNames.length; i++) {
            msg += memberNames[i] + ", ";
        }
        msg += "are invited.";
        
        Message message = new Message();
        message.setCid(cid);
        message.setContent(msg);
        message.setReadCnt(-1);
        message.setMember(admin.get());
        message = messageRepo.save(message);

        return message;
    }


    //-----------------채팅퇴장 메시지를 만듭니다.-------------------
    public Message makeExitMessage(int cid, String name){
        Optional<Member> member = memberRepository.findByName(name);

        if(!member.isPresent()){
            logger.info("채팅방에 존재하지 않는 계정입니다.");
            return null;
        }

        Message message = new Message();
        message.setCid(cid);
        message.setMember(member.get());
        message.setContent(name + " left chat room");
        message.setReadCnt(-1); // -1은 채팅퇴장 메시지

        message = messageRepo.save(message);

        return message;

    }
}

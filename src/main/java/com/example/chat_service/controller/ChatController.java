package com.example.chat_service.controller;



import com.example.chat_service.jpa.ChatRoomRepository;
import com.example.chat_service.jpa.MemberRepository;
import com.example.chat_service.service.ChatService;
import com.example.chat_service.vo.ChatRoom;
import com.example.chat_service.vo.Member;
import com.example.chat_service.vo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@CrossOrigin
@RestController
public class ChatController {

    private static final Logger logger =LoggerFactory.getLogger(ChatController.class);

    @Autowired
    ChatService chatService;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    MemberRepository memberRepository;


    //-----------채팅 View를 리턴합니다..-----------
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ChatRoom view(HttpServletRequest request, Model model){
        Optional<Member> member = memberRepository.findByEmail("test@naver.com");
        ChatRoom chatRoom = chatService.loadRoomInfo(16);
        model.addAttribute("chatRoom", chatRoom);
        List<ChatRoom> chatRoomlist = chatService.loadChatList(member.get());
        model.addAttribute("RoomList",chatRoomlist);
        return chatRoom;
    }

    //-----------채팅방을 만듭니다.-----------
    @ResponseBody
    @RequestMapping(value = "create_room", method = RequestMethod.POST)
    public ChatRoom createRoom(String roomName,
                             @RequestParam("memberNames[]") String[] memberNames){
        logger.info("Create Room");
        ChatRoom chatRoom = chatService.createRoom(roomName, memberNames);
        return chatRoom;
    }

    //-----------채팅방 삭제합니다..-----------
    @MessageMapping(value = "exit_room/{roomId}")
    @SendTo("/topic/message/{roomId}")
    public Message exitRoom(@DestinationVariable String roomId,
                            String memberName){

        logger.info("Exit Room");
        logger.info("Room Id : "+roomId);
        logger.info("Name    : "+memberName);
        int intRoomId = Integer.parseInt(roomId);
        boolean isExit = chatService.exitRoom(intRoomId, memberName);

        if(isExit){
            logger.info("Exit Success");
            Message message = chatService.makeExitMessage(intRoomId, memberName);
            return message;
        }

        else logger.info("Exit Failed");
        return null;
    }
    //-----------채팅방 기록들을 모델에 담고 채팅방 화면을 리턴합니다.-----------
    @ResponseBody // 객체를 json형식으로 변환하여 던져준다.
    @RequestMapping(value = "chatRoom", method = RequestMethod.POST)  // Todo 맵핑수정 필요
    public ChatRoom enterRoom(Integer chatRoomId){
        logger.info("EnterRoom");
        System.out.println(chatRoomId);
        ChatRoom chatRoom = chatService.loadRoomInfo(chatRoomId);
        return chatRoom;
    }

    //-----------채팅방리스트를 리턴합니다.-----------
    @ResponseBody // 객체를 json형식으로 변환하여 던져준다.
    @RequestMapping(value = "chat_list", method = RequestMethod.POST)
    public List<ChatRoom> loadList(Member member){
        logger.info("loadList");
        List<ChatRoom> chatRooms = chatService.loadChatList(member);
        return chatRooms;
    }

    //-----------채팅 메시지를 저장하고, 구독중인 클라이언트들에게 뿌려줍니다.-----------
    @ResponseBody
    @MessageMapping("chat/{roomId}")
    @SendTo("/topic/message/{roomId}")
    public Message chat(Message message, @RequestBody Map<String,String> data,
                        @DestinationVariable String roomId){
        logger.info("Chat in : " + roomId);
        String sender = data.get("userName");
        logger.info("UserName : " + sender);
        chatService.saveMessage(message,sender);
        return message;
    }

    @MessageMapping("info")
    @SendToUser("/queue/info")
    public String info(String message, SimpMessageHeaderAccessor messageHeaderAccessor){
        System.out.println("Info In");
        return message;
    }

}
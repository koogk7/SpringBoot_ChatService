package com.example.chat_service.controller;


import com.example.chat_service.ChattingHistoryDAO;
import com.example.chat_service.ChattingMessage;
import com.example.chat_service.Receiver;
import com.example.chat_service.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ChattingController {

    @Autowired
    private Sender sender;

    @Autowired
    private Receiver receiver;

    @Autowired
    private ChattingHistoryDAO chattingHistoryDAO;

    private static String BOOT_TOPIC = "kafka-chatting";

    @MessageMapping("/message")
    public void sendMessage(ChattingMessage message) throws Exception {
        message.setTimeStamp(System.currentTimeMillis());
        chattingHistoryDAO.save(message);
        sender.send(BOOT_TOPIC, message);
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET, produces = "application/json")
    public List<ChattingMessage> getChattingHistory() throws Exception {
        System.out.println("History");
        return chattingHistoryDAO.get();
    }

    @MessageMapping("/file")
    @SendTo("/topic/chatting")
    public ChattingMessage sendFile(ChattingMessage message) throws Exception{
        return new ChattingMessage(message.getFileName(), message.getRawData(), message.getUser());
    }

}

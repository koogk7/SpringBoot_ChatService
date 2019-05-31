package com.example.chat_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// service와 컴포넌트의 차이 정리하기
@Component
public class Sender {
    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    @Autowired
    private KafkaTemplate<String, ChattingMessage> kafkaTemplate;

    public void send(String topic, ChattingMessage data){
        LOGGER.info("sending data='{}' to topic='{}'", data, topic);
        kafkaTemplate.send(topic,data);
    }

}

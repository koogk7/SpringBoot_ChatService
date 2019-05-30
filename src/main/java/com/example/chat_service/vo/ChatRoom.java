package com.example.chat_service.vo;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cid;
    private String name;

    @OneToMany
    @JoinColumn(name = "cid")
    private List<Message> messages;

    @OneToMany
    @JoinColumn(name = "cid")
    private List<ChatMember> members;

    public ChatRoom(String _name){
        this.name = _name;
    }

}

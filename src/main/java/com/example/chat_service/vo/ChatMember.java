package com.example.chat_service.vo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "chat_member")
public class ChatMember implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cmid;
    private int cid;
    private String role;

    @OneToOne
    @JoinColumn(name = "uid")
    private Member member;

}

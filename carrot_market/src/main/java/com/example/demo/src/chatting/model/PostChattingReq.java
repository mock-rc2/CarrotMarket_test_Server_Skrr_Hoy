package com.example.demo.src.chatting.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostChattingReq {
    private int buyerUserId;
    private int postId;
}

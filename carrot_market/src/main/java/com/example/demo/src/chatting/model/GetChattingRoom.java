package com.example.demo.src.chatting.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChattingRoom {
    private int ChattingRoomId;
    private int otherUserId;
    private int postId;
}

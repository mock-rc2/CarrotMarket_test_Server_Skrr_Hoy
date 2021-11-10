package com.example.demo.src.chatting.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchChattingRoomStatus {
    private int userId;
    private int chattingRoomId;
    private String status;
}

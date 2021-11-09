package com.example.demo.src.chatting.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChattingContent {
    private int chattingContentId;
    private int userId;
    private String content;
    private String time;
    private String day;
}

package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Post {
    private int userId;
    private String phoneNumber;
    private String nickName;
    private String email;
    private String image;
}

package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String city;
    private String district;
    private String townName;
    private String phoneNumber;
    private String nickName;
    private String image;
}

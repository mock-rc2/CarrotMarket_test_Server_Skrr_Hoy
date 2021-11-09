package com.example.demo.src.post.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AllPostSelectRes {
    private int postId;
    private int townId;
    private String title;
    private int categoryId;
    private int cost;
    private String content;
    private String status;

}

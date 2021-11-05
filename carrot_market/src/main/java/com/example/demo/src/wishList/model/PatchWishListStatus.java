package com.example.demo.src.wishList.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchWishListStatus {
    private int userId;
    private int wishListId;
    private String status;
}

package com.example.demo.src.address.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class GetAddressRes {

    private int townId;
    private String certification;
    private int range;

}

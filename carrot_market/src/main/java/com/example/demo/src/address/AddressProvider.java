package com.example.demo.src.address;


import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class AddressProvider {

    private final AddressDao addressDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public AddressProvider(AddressDao addressDao, JwtService jwtService) {
        this.addressDao = addressDao;
        this.jwtService = jwtService;
    }

}

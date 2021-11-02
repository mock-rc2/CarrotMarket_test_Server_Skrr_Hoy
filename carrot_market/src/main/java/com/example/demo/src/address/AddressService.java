package com.example.demo.src.address;



import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class AddressService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AddressDao addressDao;
    private final AddressProvider addressProvider;
    private final JwtService jwtService;


    @Autowired
    public AddressService(AddressDao addressDao, AddressProvider addressProvider, JwtService jwtService) {
        this.addressDao = addressDao;
        this.addressProvider = addressProvider;
        this.jwtService = jwtService;

    }

}

package com.example.demo.src.address;



import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.*;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

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

    public void postAddress(int userId, int townId) throws BaseException {
            // 1. 현재 설정된 동네 갯수 파악 -> 현재 설정된 2개 미만이라면 동네 추가 가능
            // 2. 이미 저장되어있었던 동네인 지 확인
            // 3. 저장되어있다면 status Valid로 바꿈
            // 4. 저장되어있지 않다면

        // 1. 현재 설정된 동네 갯수 파악
        if (addressDao.countUserAddress(userId) >= 2) {
            throw new BaseException(POST_CREATE_ADDRESS_ERROR);
        }

        try {
            // 2. 이미 저장되어있던 동네라면
            if(addressDao.getIsExistAddress(userId, townId) == 1){
                    //3-1. addressId 가져오기
                    int addressId = addressDao.getAddressId(userId, townId);

                    //3-2. addressId에 해당하는 status Valid로 바꾸기
                    addressDao.patchAddressStatus(addressId);
            }
            // 4. 저장되어있지 않은 동네라면
            else{
                //4-1. 새로 생성하기
                addressDao.createAddress(userId,townId);
            }
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}

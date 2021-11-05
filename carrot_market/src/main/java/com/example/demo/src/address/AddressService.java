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
        int stateValidTown = addressDao.countUserAddress(userId);
        if (stateValidTown >= 2) {
            throw new BaseException(CREATE_ADDRESS_ERROR);
        }

        // 1-1 현재 설정된 동네가 존재할 때 -> 현재 설정된 동네의 selectAddress 를 Invalid로 바꿔야함
        if (stateValidTown == 1) {
            try {
                addressDao.patchSelectAddress(userId);
            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }


        try {
            // 2. 이미 저장되어있던 동네라면
            if (addressDao.getIsExistAddress(userId, townId) == 1) {
                //3-1. addressId 가져오기
                int addressId = addressDao.getAddressId(userId, townId);

                //3-2. addressId에 해당하는 status Valid로 바꾸기, selectAddress = Valid 처리
                addressDao.patchAddressStatusValid(addressId);
            }
            // 4. 저장되어있지 않은 동네라면
            else {
                //4-1. 새로 생성하기
                addressDao.createAddress(userId, townId);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void patchAddress(int userId,int townId) throws BaseException {
        // 1. address에 userId 와 townId가 존재하는 행이 Valid 상태인지 확인 (현재 선택된 동네인지 확인)
        // 2. 1이 확인된다면 status = inValid, selectAddress = Invalid 상태로 바꾸기


        //1. 현재 선택된 상태의 동네인지 확인
        if (addressDao.isSelectedTown(userId,townId) == 0) {
            throw new BaseException(INVALID_ADDRESS_ERROR);
        }

        //2. status = Invalid, selectAddress =Invalid 로 수정
        try{
            int addressId = addressDao.getAddressId(userId, townId);

            //3-2. addressId에 해당하는 status Invalid, selectAddress = Invalid로 바꾸기
            addressDao.patchAddressStatusInvalid(addressId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
        try {
            // 현재 선택된 동네가 남아있다면
            int stateValidTown = addressDao.countUserAddress(userId);
            if (stateValidTown == 1) {
                //삭제되지않은 동네 selectAddress = Valid로 바꿔주기
                int addressId = addressDao.getAddressIdByState(userId);
                addressDao.patchAddressStatusValid(addressId);
            }
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

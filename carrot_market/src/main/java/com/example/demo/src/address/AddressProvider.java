package com.example.demo.src.address;


import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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


    public List<GetTownRes> getTownBySearch(String search) throws BaseException {


        // 1. 토큰을 이용해 유저가 현재 선택하고 있는 동네id를 가져온다.
        // 2. 승인된 동네인지 확인한다.
        // 3. 승인된 주소라면 getTownOrderByAddress() 실행
        // 4. 승인되지 않은 주소라면 getTownOrderByName() 실행
            // 1
            int userIdByJwt = jwtService.getUserId();
            int townId = addressDao.getTownIdByUserId(userIdByJwt);

        List<GetTownRes> getTownRes;
        GetLocation getLoc;
        try {

            //2 승인된 동네인지 확인
            if(addressDao.isCertifiedAddress(userIdByJwt, townId).equals("Valid")){
                getLoc = addressDao.getLocation(townId);
                getTownRes = addressDao.getTownOrderByAddress(search, getLoc.getLat(),getLoc.getLng());
            }
            //3 승인되지 않은 동네일 때
            else{
                getTownRes = addressDao.getTownOrderByName(search);
            }

            return getTownRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



    public List<GetTownRes> getTownByLocation(GetTownReq getTownReq) throws BaseException {
        // 1. 입력된 동네 정보가 존재하는 지 확인
        if(addressDao.getTownExist(getTownReq) == 0){
            throw new BaseException(GET_TOWN_EXIST_ERROR);
        }

        // 2. 입력된 동네 정보의 townId 와 lat, lng 찾기
        int townId = addressDao.getTownIdByGetTownReq(getTownReq);
        GetLocation getLoc = addressDao.getLocation(townId);



        // 3. 현재 동네에서 가까운 순서대로 정렬한 리스트를 반환
        List<GetTownRes> getTownRes;
        try{
            getTownRes = addressDao.getNearTownOrderByName(getLoc.getLat(),getLoc.getLng());
            return getTownRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }


    }

}

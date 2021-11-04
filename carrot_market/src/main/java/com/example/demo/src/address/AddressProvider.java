package com.example.demo.src.address;


import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import java.util.ArrayList;
=======
>>>>>>> hoy_branch
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


<<<<<<< HEAD
    public List<GetTownRes> getTownBySearch(String search, int townId) throws BaseException {

        // 1. townId가 존재한다면 getTownOrderByAddress() 실행
        // 2. townId가 존재하지 않는다면 getTownOrderByName() 실행
=======
    public List<GetTownRes> getTownBySearch(String search) throws BaseException {


        // 1. 토큰을 이용해 유저가 현재 선택하고 있는 동네id를 가져온다.
        // 2. 승인된 동네인지 확인한다.
        // 3. 승인된 주소라면 getTownOrderByAddress() 실행
        // 4. 승인되지 않은 주소라면 getTownOrderByName() 실행
            // 1
            int userIdByJwt = jwtService.getUserId();
            int townId = addressDao.getTownIdByUserId(userIdByJwt);
>>>>>>> hoy_branch

        List<GetTownRes> getTownRes;
        GetLocation getLoc;
        try {

<<<<<<< HEAD
            //1. townId가 존재한다면
            if(townId != -1){
                getLoc = addressDao.getLocation(townId);
                getTownRes = addressDao.getTownOrderByAddress(search, getLoc.getLat(),getLoc.getLng());
            }
            //2 townId가 존재하지 않는다면
=======
            //2 승인된 동네인지 확인
            if(addressDao.isCertifiedAddress(userIdByJwt, townId).equals("Valid")){
                getLoc = addressDao.getLocation(townId);
                getTownRes = addressDao.getTownOrderByAddress(search, getLoc.getLat(),getLoc.getLng());
            }
            //3 승인되지 않은 동네일 때
>>>>>>> hoy_branch
            else{
                getTownRes = addressDao.getTownOrderByName(search);
            }

            return getTownRes;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }



<<<<<<< HEAD
    public List<GetTownRes> getTownByLocation(int townId) throws BaseException {

        // 1. 입력된 townId를 이용하여 lat, lng 찾기
        GetLocation getLoc = addressDao.getLocation(townId);

=======
    public List<GetTownRes> getTownByLocation(GetTownReq getTownReq) throws BaseException {
        // 1. 입력된 동네 정보가 존재하는 지 확인
        if(addressDao.getTownExist(getTownReq) == 0){
            throw new BaseException(GET_TOWN_EXIST_ERROR);
        }

        // 2. 입력된 동네 정보의 townId 와 lat, lng 찾기
        int townId = addressDao.getTownIdByGetTownReq(getTownReq);
        GetLocation getLoc = addressDao.getLocation(townId);



>>>>>>> hoy_branch
        // 3. 현재 동네에서 가까운 순서대로 정렬한 리스트를 반환
        List<GetTownRes> getTownRes;
        try{
            getTownRes = addressDao.getNearTownOrderByName(getLoc.getLat(),getLoc.getLng());
            return getTownRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

<<<<<<< HEAD
    }
    public int getTownId(String city, String district,String townName) throws BaseException {
        //1. 현재 위치의 townId 반환
        int townId;
        try {
            townId = addressDao.getTownId(city, district, townName);
            return townId;
        }catch(Exception exception){
            throw new BaseException(GET_TOWN_EXIST_ERROR);
        }
    }

    public GetNearTownListRes getNearTownList(int townId) throws BaseException {
        // 1. townId의 lat와 lng를 구한다.
        // 2. townId가 서울에 속하면 r1, r2, r3,r4 는 각각 1,2,3,4 km의 영역
        //    서울이 아니면 r1,r2,r3,r4는 각각 2,5,8,10km의 영역
        // 3. range별 townId List 만들기
        // 4. getNearTownListRes에 넣고 반환
        GetLocation getLoc;

        ArrayList<Integer> r1;
        ArrayList<Integer> r2;
        ArrayList<Integer> r3;
        ArrayList<Integer> r4;
        if ((1 <= townId && townId <= 424) || (3547 <= townId && townId <= 3928)) {
            try {
                getLoc = addressDao.getLocation(townId);
                r1 = addressDao.getNearTownListByRange(1, getLoc.getLat(),getLoc.getLng());
                r2 = addressDao.getNearTownListByRange(2, getLoc.getLat(),getLoc.getLng() );
                r3 = addressDao.getNearTownListByRange(3, getLoc.getLat(),getLoc.getLng());
                r4 = addressDao.getNearTownListByRange(4, getLoc.getLat(),getLoc.getLng());

            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        } else {

            try {
                getLoc = addressDao.getLocation(townId);
                r1 = addressDao.getNearTownListByRange(2, getLoc.getLat(),getLoc.getLng());
                r2 = addressDao.getNearTownListByRange(4, getLoc.getLat(),getLoc.getLng());
                r3 = addressDao.getNearTownListByRange(6, getLoc.getLat(),getLoc.getLng());
                r4 = addressDao.getNearTownListByRange(8, getLoc.getLat(),getLoc.getLng());

            } catch (Exception exception) {
                throw new BaseException(DATABASE_ERROR);
            }
        }
        GetNearTownListRes getNearTownListRes = new GetNearTownListRes(r1,r2,r3,r4);
        return getNearTownListRes;
    }
=======

    }

>>>>>>> hoy_branch
}

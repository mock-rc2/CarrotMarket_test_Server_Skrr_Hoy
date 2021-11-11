package com.example.demo.src.user;



import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    //POST
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        
        // 겹치는 폰 번호가 있다면
        if(userDao.checkPhoneNumber(postUserReq.getPhoneNumber()) == 1){
            throw new BaseException(POST_USERS_DUPLICATE_PHONENUMBER);
        }

        try{



            int townId = userDao.getTownId(postUserReq);


            int leftLimit = 48; // numeral '0'
            int rightLimit = 57; // letter '9'
            int targetStringLength = 6;
            Random random = new Random();
            String certificationNum = random.ints(leftLimit,rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();

            int userId = userDao.createUser(postUserReq, certificationNum);

            //주소 삽입
            userDao.createAddress(userId,townId);
            userDao.createCategory(userId);

            //jwt 발급.
            String jwt = jwtService.createJwt(userId);
            return new PostUserRes(userId,jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void patchUserProfile(int userId, PatchUserProfileReq patchUserProfileReq) throws BaseException {
        // 1. userId가 유효한 지 확인 ( status = Valid)
        // 2. 닉네임이 다른지 확인
        // 3. 닉네임 변경한지 30일 이후인지 확인
        // 4. 유저 정보 수정

        //1.
        if (userDao.checkUserExist(userId) == 0) {
            throw new BaseException(POST_POST_INVALID_USER);
        }
        //2.

        if(!userDao.checkUserNickName(userId).equals(patchUserProfileReq.getNickName())){
            //3
            if( userDao.checkNickNameUpdated(userId) < 30){
                throw new BaseException(NICKNAME_UPDATED_ERROR) ;
            }
            //4
            userDao.patchUserProfile(userId, patchUserProfileReq);
        }else{
            //4
            userDao.patchUserProfileImage(userId, patchUserProfileReq.getImage());
        }
    }

    void saveCertificationInfo(String phoneNumber, String certificationNum){
        userDao.saveCertificationInfo(phoneNumber, certificationNum);
    }

}

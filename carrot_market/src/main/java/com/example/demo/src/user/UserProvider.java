package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }


    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException{

        try{
        User user = userDao.getUsersByPhoneNumber(postLoginReq);
        int userId = user.getUserId();
        String jwt = jwtService.createJwt(userId);
        return new PostLoginRes(userId,jwt);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //유저 상태 체크
    public int checkStatus(String phoneNumber) throws BaseException{
        try{
            return userDao.checkStatus(phoneNumber);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //인증번호 일치 여부 체크
    public int checkCertificationNum(PostLoginReq postLoginReq) throws BaseException{
        try{
            return userDao.checkCertificationNum(postLoginReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public GetUserRes getUser(int userId) throws BaseException{
        // 1. userId가 유효한 지 확인 ( status = Valid)
        // 2. userId를 통해 유저 정보 조회

        //1.
        if (userDao.checkUserExist(userId) == 0) {
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            GetUserRes getUserRes = userDao.getUser(userId);
            return getUserRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public int getUserNickNameUpdated(int userId) throws BaseException{
        try {
            return 30 - userDao.checkNickNameUpdated(userId);
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetUserAccountRes getUserAccount(int userId) throws BaseException {


        try {
            GetUserAccountRes getUserAccount = userDao.getUserAccount(userId);
            return getUserAccount;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }


    }

    public PostLoginRes logInJwt() throws BaseException{

        //jwt에서 idx 추출.
        int userId = jwtService.getUserId();

        int checkUserId = userDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{

            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(userId,jwt);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public int checkCertificationNumByOauth(PostLoginReq postLoginReq) throws BaseException{
        try{
            return userDao.checkCertificationNumByOauth(postLoginReq);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

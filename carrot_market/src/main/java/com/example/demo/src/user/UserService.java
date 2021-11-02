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
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

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

        try{

            if(userDao.checkPhoneNumber(postUserReq.getPhoneNumber()) == 1){
                throw new BaseException(POST_USERS_DUPLICATE_PHONENUMBER);
            }

            int townId = userDao.getTownId(postUserReq);
            // townId가 null이라면? -> error catch?
            int userId = userDao.createUser(postUserReq);

            //주소 삽입
            int addressId = userDao.createAddress(userId,townId);

            //default 선택 주소 삽입
            userDao.createAddressUser(userId,addressId);

            //jwt 발급.
            String jwt = jwtService.createJwt(userId);
            return new PostUserRes(userId,jwt);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }


}

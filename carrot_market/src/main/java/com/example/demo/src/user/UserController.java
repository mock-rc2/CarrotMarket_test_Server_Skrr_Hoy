package com.example.demo.src.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/users")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;




    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {

        if(postUserReq.getCity() == null || postUserReq.getDistrict() == null || postUserReq.getTownName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_ADDRESS);
        }
        if(postUserReq.getPhoneNumber() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PHONENUMBER);
        }
        if(postUserReq.getNickName() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }

        if(!isRegexPhone(postUserReq.getPhoneNumber())){
            return new BaseResponse<>(POST_USERS_INVALID_PHONE);
        }


        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }




    /**
     * 로그인 API
     * [POST] /users/logIn
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @PostMapping("/logIn")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq){
        try{

            //휴대폰번호 입력 체크
            if(postLoginReq.getPhoneNumber() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
            }
            //휴대폰 정규표현
            if(!isRegexPhone(postLoginReq.getPhoneNumber())){
                return new BaseResponse<>(POST_USERS_INVALID_PHONE);
            }
            //인증번호 입력 체크
            if(postLoginReq.getCertificationNum() == null){
                return new BaseResponse<>(POST_USERS_EMPTY_CERTIFICATIONNUM);
            }
            //정상 상태 유저인지 체크
            //정상 상태가 아니라면 -> 회원가입으로 유도
            int checkStatus = userProvider.checkStatus(postLoginReq.getPhoneNumber());
            if(checkStatus == 0){//정상 상태가 아닌 유저라면
                return new BaseResponse<>(POST_USERS_INVALID_USER);
            }

            //인증번호가 일치하는지 체크
            int checkCertificationNum = userProvider.checkCertificationNum(postLoginReq);
            if(checkCertificationNum == 0){//정상 상태가 아닌 유저라면
                return new BaseResponse<>(POST_USERS_INVALID_CERTIFICATIONNUM);
            }

            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }




}

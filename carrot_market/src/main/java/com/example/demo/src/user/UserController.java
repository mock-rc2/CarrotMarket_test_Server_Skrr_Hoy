package com.example.demo.src.user;

import com.example.demo.src.address.model.GetTownNameRes;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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



    /**
     * 유저의 정보 조회
     * [GET] /users/:userId
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<GetUserRes> getUserAddress(@PathVariable("userId") int userId) throws BaseException {

        try {
            GetUserRes getUserRes = userProvider.getUser(userId);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 유저 프로필 수정
     * [Patch] /users/profile/{userId}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/profile/{userId}")
    public BaseResponse<String> patchUserProfile(@RequestBody PatchUserProfileReq patchUserProfileReq, @PathVariable("userId") int userId){

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        int userIdByJwt;

        try {
            //권한 확인
            userIdByJwt = jwtService.getUserId();
            if(userIdByJwt != userId){
                throw new BaseException(INVALID_USER_JWT);
            }
            // 닉네임 길이 확인
            if(patchUserProfileReq.getNickName().length() < 2 || patchUserProfileReq.getNickName().length() > 12){
                throw new BaseException(NICKNAME_LENGTH_ERROR);
            }
            //프로필 수정
            userService.patchUserProfile(userId, patchUserProfileReq);

            String result = "유저 정보가 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 유저의 남은 닉네임 변경 불가 기간 조회
     * [GET] /users/nickname-updated/{userId}
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/nickname-updated/{userId}")
    public BaseResponse<Integer> getUserNickNameUpdated(@PathVariable("userId") int userId) throws BaseException {

        try {
            int getUserNickNameUpdated = userProvider.getUserNickNameUpdated(userId);
            return new BaseResponse<>(getUserNickNameUpdated);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 유저의 계정 정보 조회
     * [GET] /users/account/{userId}
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/account/{userId}")
    public BaseResponse<GetUserAccountRes> getUserAccount(@PathVariable("userId") int userId) throws BaseException {

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }


        try {
            //권한 확인
            int userIdByJwt = jwtService.getUserId();
            if(userIdByJwt != userId){
                throw new BaseException(INVALID_USER_JWT);
            }

            GetUserAccountRes getUserAccountRes = userProvider.getUserAccount(userId);
            return new BaseResponse<>(getUserAccountRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 자동 로그인 API
     * [GET] /users/logIn/jwt
     * @return BaseResponse<PostLoginRes>
     */
    @ResponseBody
    @GetMapping("/logIn/jwt")
    public BaseResponse<PostLoginRes> logInJwt(){

        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
        try{

            PostLoginRes postLoginRes = userProvider.logInJwt();
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("/logIn/message/{phoneNumber}")
    public void testSend(@PathVariable("phoneNumber") String phoneNumber) {
        String api_key = ""; //사이트에서 발급 받은 API KEY
        String api_secret = ""; //사이트에서 발급 받은 API
        Message coolsms = new Message(api_key, api_secret);
        HashMap<String, String> params = new HashMap<String, String>();
        //난수 생성
        int leftLimit = 48; // numeral '0'
        int rightLimit = 57; // letter '9'
        int targetStringLength = 6;
        Random random = new Random();
        String certificationNum = random.ints(leftLimit,rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        //DB저장
        userService.saveCertificationInfo(phoneNumber,certificationNum);
        params.put("to", phoneNumber);
        params.put("from", phoneNumber); //사전에 사이트에서 번호를 인증하고 등록하여야 함
        params.put("type", "SMS");
        params.put("text", "인증 번호는 "+ certificationNum + " 입니다."); //메시지 내용
        params.put("app_version", "test app 1.2");
        try { JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString()); //전송 결과 출력
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }

    @ResponseBody
    @PostMapping("/logIn/message")
    public BaseResponse<PostLoginRes> logInByOauth(@RequestBody PostLoginReq postLoginReq){
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

            //인증번호가 일치하는지 체크 - Ouath이용
            int checkCertificationNum = userProvider.checkCertificationNumByOauth(postLoginReq);
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

package com.example.demo.src.chatting;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.*;
import com.example.demo.src.chatting.model.*;
import com.example.demo.src.post.model.AllPostSelectRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class ChattingProvider {
    private final ChattingDao chattingDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ChattingProvider(ChattingDao chattingDao, JwtService jwtService) {
        this.chattingDao = chattingDao;
        this.jwtService = jwtService;
    }

    public List<GetChattingRoom> allChattingSelect(int userId) throws BaseException{
        int checkUserId = chattingDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<GetChattingRoom> allChattingSelect = chattingDao.allChattingSelect(userId);//getUser(userIdx)를 반환받아서 반환한다.
            return allChattingSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PostChattingCountRes postChattingCount(int postId) throws BaseException{
        int checkPostId = chattingDao.checkPostId(postId);
        if(checkPostId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_POST);
        }
        try{
            PostChattingCountRes postChattingCount = chattingDao.postChattingCount(postId);//getUser(userIdx)를 반환받아서 반환한다.
            return postChattingCount;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public GetChattingContent getChattingContent(int chattingRoomId) throws BaseException{
        try{
            GetChattingContent getChattingContent = chattingDao.getChattingContent(chattingRoomId);//getUser(userIdx)를 반환받아서 반환한다.
            return getChattingContent;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public List<GetChattingContent> getAllChattingContent(int chattingRoomId) throws BaseException{

        try{
            List<GetChattingContent> getAllChattingContent = chattingDao.getAllChattingContent(chattingRoomId);//getUser(userIdx)를 반환받아서 반환한다.
            return getAllChattingContent;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }




}

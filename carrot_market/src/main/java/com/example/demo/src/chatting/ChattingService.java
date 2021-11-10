package com.example.demo.src.chatting;



import com.example.demo.config.BaseException;
import com.example.demo.src.chatting.model.*;
import com.example.demo.src.post.model.PatchPostStatus;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class ChattingService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ChattingDao chattingDao;
    private final ChattingProvider chattingProvider;
    private final JwtService jwtService;


    @Autowired
    public ChattingService(ChattingDao chattingDao, ChattingProvider chattingProvider, JwtService jwtService) {
        this.chattingDao = chattingDao;
        this.chattingProvider = chattingProvider;
        this.jwtService = jwtService;

    }

    public PostChattingRes createChattingRoom(int sellerUserId,PostChattingReq postChattingReq) throws BaseException{

        try{
            int chattingRoomId = chattingDao.createChattingRoom(sellerUserId,postChattingReq);
            return new PostChattingRes(chattingRoomId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void modifyChattingRoomStatus(PatchChattingRoomStatus patchChattingRoomStatus) throws BaseException {
        int checkUserId = chattingDao.checkUserId(patchChattingRoomStatus.getUserId());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }

        try{

            int result = chattingDao.modifyChattingRoomStatus(patchChattingRoomStatus);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_WISHLIST_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostChattingContentRes createChattingContent(int chattingRoomId, int userId,PostChattingContentReq postChattingContentReq) throws BaseException{

        try{
            int chattingContentId = chattingDao.createChattingContent(chattingRoomId, userId,postChattingContentReq);
            return new PostChattingContentRes(chattingContentId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }





}

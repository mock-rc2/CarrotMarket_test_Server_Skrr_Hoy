package com.example.demo.src.wishList;


import com.example.demo.config.BaseException;
import com.example.demo.src.wishList.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class WishListService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WishListDao wishListDao;
    private final WishListProvider wishListProvider;
    private final JwtService jwtService;


    @Autowired
    public WishListService(WishListDao wishListDao,WishListProvider wishListProvider, JwtService jwtService) {
        this.wishListDao = wishListDao;
        this.wishListProvider = wishListProvider;
        this.jwtService = jwtService;

    }

    //관심 목록 추가
    public PostWishListRes createWishList(PostWishListReq postWishListReq) throws BaseException {
        //유저가 본인인지 확인
        /*int userId = jwtService.getUserId();
        if(userId != postWishListReq.getUserId()) {
            throw new BaseException(POST_POST_INVALID_USER);
        }*/
        //유저가 정상인지 확인
        int checkUserId = wishListDao.checkUserId(postWishListReq.getUserId());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        //조회가 가능한 게시물인지 확인
        int checkPostId = wishListDao.checkPostId(postWishListReq.getPostId());
        if(checkPostId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_POST);
        }

        try{
            int wishListId = wishListDao.createWishList(postWishListReq);//getUser(userIdx)를 반환받아서 반환한다.
            return new PostWishListRes(wishListId);
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    public void modifyWishListStatus(PatchWishListStatus patchWishListStatus) throws BaseException {
        int checkUserId = wishListDao.checkUserId(patchWishListStatus.getUserId());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        int checkStatus = wishListDao.checkStatus(patchWishListStatus.getWishListId());
        if(checkStatus == 0){//이미 삭제됐는지
            throw new BaseException(MODIFY_FAIL_INVALID_STATUS);
        }

        int checkUserWish = wishListDao.checkUserWish(patchWishListStatus);
        if(checkUserWish == 0){//이미 삭제됐는지
            throw new BaseException(MODIFY_FAIL_INVALID_USER_WISHLIST);
        }
        try{

            int result = wishListDao.modifyWishListStatus(patchWishListStatus);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_WISHLIST_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }




}

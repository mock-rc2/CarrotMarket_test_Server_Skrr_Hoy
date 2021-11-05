package com.example.demo.src.wishList;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.wishList.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service
public class WishListProvider {

    private final WishListDao wishListDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public WishListProvider(WishListDao wishListDao, JwtService jwtService) {
        this.wishListDao = wishListDao;
        this.jwtService = jwtService;
    }

    //유저 관심 목록 조회
    public List<WishListSelectRes> wishListSelect() throws BaseException{
        int userId = jwtService.getUserId();
        int checkUserId = wishListDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        //게시물이 없는지 조회

        try{
            List<WishListSelectRes> wishListSelect = wishListDao.wishListSelect(userId);//getUser(userIdx)를 반환받아서 반환한다.
            return wishListSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //특정 게시물 관심 목록 개수 조회
    public WishListCount wishListCount(int postId) throws BaseException{
        //조회가 가능한 게시물인지 확인
        int checkPostId = wishListDao.checkPostId(postId);
        if(checkPostId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_POST);
        }

        try{
            WishListCount wishListCount = wishListDao.wishListCount(postId);//getUser(userIdx)를 반환받아서 반환한다.
            return wishListCount;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    /*
    //특정 유저 판매중 게시물 조회
    public List<AllWishListSelectRes> saleWishListSelect(int userId) throws BaseException{
        int checkUserId = wishListDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<AllWishListSelectRes> saleWishListSelect = wishListDao.saleWishListSelect(userId);//getUser(userIdx)를 반환받아서 반환한다.
            return saleWishListSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    //특정 유저 판매완료 게시물 조회
    public List<AllWishListSelectRes> dealCompleteWishListSelect(int sellerUserId) throws BaseException{
        int checkUserId = wishListDao.checkUserId(sellerUserId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<AllWishListSelectRes> dealCompleteWishListSelect = wishListDao.dealCompleteWishListSelect(sellerUserId);//getUser(userIdx)를 반환받아서 반환한다.
            return dealCompleteWishListSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //특정 유저 숨김 게시물 조회
    public List<AllWishListSelectRes> hideWishListSelect(int userId) throws BaseException{
        int checkUserId = wishListDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<AllWishListSelectRes> hideWishListSelect = wishListDao.hideWishListSelect(userId);//getUser(userIdx)를 반환받아서 반환한다.
            return hideWishListSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

     */



}

package com.example.demo.src.post;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.address.*;
import com.example.demo.src.address.model.GetNearTownListRes;
import com.example.demo.src.category.CategoryProvider;
import com.example.demo.src.category.model.GetCategoryRes;
import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.User;
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
public class PostProvider {
    private final AddressProvider addressProvider;
    private final CategoryProvider categoryProvider;
    private final PostDao postDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public PostProvider(AddressProvider addressProvider, CategoryProvider categoryProvider,PostDao postDao, JwtService jwtService) {
        this.addressProvider = addressProvider;
        this.categoryProvider = categoryProvider;//주소 사용하기 위해 추가
        this.postDao = postDao;
        this.jwtService = jwtService;
    }

    //특정유저 전체 판매중, 판매완료 게시물 조회
    public List<AllPostSelectRes> allPostSelect(int userId) throws BaseException{
        int checkUserId = postDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        //게시물이 없는지 조회

        try{
            List<AllPostSelectRes> allPostSelectRes = postDao.allPostSelect(userId);//getUser(userIdx)를 반환받아서 반환한다.
            return allPostSelectRes;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    //특정 유저 판매중 게시물 조회
    public List<AllPostSelectRes> salePostSelect(int userId) throws BaseException{
        int checkUserId = postDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<AllPostSelectRes> salePostSelect = postDao.salePostSelect(userId);//getUser(userIdx)를 반환받아서 반환한다.
            return salePostSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
    //특정 유저 판매완료 게시물 조회
    public List<AllPostSelectRes> dealCompletePostSelect(int sellerUserId) throws BaseException{
        int checkUserId = postDao.checkUserId(sellerUserId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<AllPostSelectRes> dealCompletePostSelect = postDao.dealCompletePostSelect(sellerUserId);//getUser(userIdx)를 반환받아서 반환한다.
            return dealCompletePostSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //특정 유저 숨김 게시물 조회
    public List<AllPostSelectRes> hidePostSelect(int userId) throws BaseException{
        int checkUserId = postDao.checkUserId(userId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<AllPostSelectRes> hidePostSelect = postDao.hidePostSelect(userId);//getUser(userIdx)를 반환받아서 반환한다.
            return hidePostSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //특정 유저 구매완료 게시물 조회
    public List<AllPostSelectRes> purchaseCompletePostSelect(int buyerUserId) throws BaseException{
        int checkUserId = postDao.checkUserId(buyerUserId);
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        try{
            List<AllPostSelectRes> purchaseCompletePostSelect = postDao.purchaseCompletePostSelect(buyerUserId);//getUser(userIdx)를 반환받아서 반환한다.
            return purchaseCompletePostSelect;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public GetPostImage getPostTitleImage(int postId) throws BaseException{
        int checkPostId = postDao.checkPostId(postId);
        if(checkPostId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_POST);
        }
        try{
            GetPostImage getPostTitleImage = postDao.getPostTitleImage(postId);//getUser(userIdx)를 반환받아서 반환한다.
            return getPostTitleImage;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public List<GetPostImage> getPostAllImage(int postId) throws BaseException{
        int checkPostId = postDao.checkPostId(postId);
        if(checkPostId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_POST);
        }
        try{
            List<GetPostImage> getPostAllImage = postDao.getPostAllImage(postId);//getUser(userIdx)를 반환받아서 반환한다.
            return getPostAllImage;
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }


    public List<PostSelectRes> getPostUseAddress(int userId, int townId, int range) throws BaseException{

        try{//range 값에 따라 다른 근처 동네를 조회해야 한다.
            GetNearTownListRes getNearTownListRes = addressProvider.getNearTownList(townId);
            List<GetCategoryRes> getUserCategoryRes  = categoryProvider.getUserCategory(userId);//홈화면에서는 관심 카테고리 별로 다른 화면을 보여줘야한다.
            List<Integer> rangeList = null;
            String selectPostQurey = "";
            String interestCategoryQurey = "";
            if(range == 0){
                rangeList = getNearTownListRes.getRange1();
            }
            else if(range == 1){
                rangeList = getNearTownListRes.getRange2();
            }
            else if(range == 2){
                rangeList = getNearTownListRes.getRange3();
            }
            else if(range == 3){
                rangeList = getNearTownListRes.getRange4();
            }
            //이 리스트에 담긴 동네에 해당하는 게시글을 조회해야한다. 카테고리는 아직 완성되지 않아서 이후 추가할것.
            for(int i=0;i<rangeList.size();i++){
                if(i == rangeList.size()-1){
                    int Idx = rangeList.get(i);
                    selectPostQurey += Idx;
                }
                else{
                    int Idx = rangeList.get(i);
                    selectPostQurey += Idx +",";
                }
            }

            for(int i=0;i<getUserCategoryRes.size();i++){
                if(i == getUserCategoryRes.size()-1){
                    int Idx = getUserCategoryRes.get(i).getCategoryId();
                    interestCategoryQurey += Idx;
                }
                else{
                    int Idx = getUserCategoryRes.get(i).getCategoryId();
                    interestCategoryQurey += Idx +",";
                }
            }


            List<PostSelectRes> getPostUseAddress = postDao.getPostUseAddress(selectPostQurey,interestCategoryQurey);
            return getPostUseAddress;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public List<PostSelectRes> getPostUseAddressByKeyword(int userId, int townId, int range, String keyword) throws BaseException{

        try{//range 값에 따라 다른 근처 동네를 조회해야 한다.
            GetNearTownListRes getNearTownListRes = addressProvider.getNearTownList(townId);
            List<Integer> rangeList = null;
            String selectPostQurey = "";
            if(range == 0){
                rangeList = getNearTownListRes.getRange1();
            }
            else if(range == 1){
                rangeList = getNearTownListRes.getRange2();
            }
            else if(range == 2){
                rangeList = getNearTownListRes.getRange3();
            }
            else if(range == 3){
                rangeList = getNearTownListRes.getRange4();
            }
            //이 리스트에 담긴 동네에 해당하는 게시글을 조회해야한다. 카테고리는 아직 완성되지 않아서 이후 추가할것.
            for(int i=0;i<rangeList.size();i++){
                if(i == rangeList.size()-1){
                    int Idx = rangeList.get(i);
                    selectPostQurey += Idx;
                }
                else{
                    int Idx = rangeList.get(i);
                    selectPostQurey += Idx +",";
                }
            }

            List<PostSelectRes> getPostUseAddressByKeyword = postDao.getPostUseAddressByKeyword(selectPostQurey,keyword);
            return getPostUseAddressByKeyword;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public List<PostSelectRes> getPostUseAddressByCategory(int userId, int townId, int range, int categoryId) throws BaseException{

        try{//range 값에 따라 다른 근처 동네를 조회해야 한다.
            GetNearTownListRes getNearTownListRes = addressProvider.getNearTownList(townId);
            List<Integer> rangeList = null;
            String selectPostQurey = "";
            if(range == 0){
                rangeList = getNearTownListRes.getRange1();
            }
            else if(range == 1){
                rangeList = getNearTownListRes.getRange2();
            }
            else if(range == 2){
                rangeList = getNearTownListRes.getRange3();
            }
            else if(range == 3){
                rangeList = getNearTownListRes.getRange4();
            }
            //이 리스트에 담긴 동네에 해당하는 게시글을 조회해야한다. 카테고리는 아직 완성되지 않아서 이후 추가할것.
            for(int i=0;i<rangeList.size();i++){
                if(i == rangeList.size()-1){
                    int Idx = rangeList.get(i);
                    selectPostQurey += Idx;
                }
                else{
                    int Idx = rangeList.get(i);
                    selectPostQurey += Idx +",";
                }
            }

            List<PostSelectRes> getPostUseAddressByCategory = postDao.getPostUseAddressByCategory(selectPostQurey,categoryId);
            return getPostUseAddressByCategory;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }


}

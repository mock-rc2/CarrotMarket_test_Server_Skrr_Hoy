package com.example.demo.src.post;



import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class PostService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final PostDao postDao;
    private final PostProvider postProvider;
    private final JwtService jwtService;


    @Autowired
    public PostService(PostDao postDao, PostProvider postProvider, JwtService jwtService) {
        this.postDao = postDao;
        this.postProvider = postProvider;
        this.jwtService = jwtService;

    }

    public PostPostRes post(PostPostReq postPostReq) throws BaseException{

        try{
            int postId = postDao.createPost(postPostReq);
            return new PostPostRes(postId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PostPostImageRes postImage(PostPostImageReq postPostImageReq) throws BaseException{

        try{
            int postImageId = postDao.createPostImage(postPostImageReq);
            return new PostPostImageRes(postImageId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void modifyPostStatus(PatchPostStatus patchPostStatus) throws BaseException {
        int checkUserId = postDao.checkUserId(patchPostStatus.getUserId());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        int checkPostStatus = postDao.checkPostStatus(patchPostStatus.getPostId());
        if(checkPostStatus == 0){//이미 삭제됐는지
            throw new BaseException(MODIFY_FAIL_INVALID_POST);
        }

        int checkUserWish = postDao.checkUserPost(patchPostStatus);
        if(checkUserWish == 0){//유저가 맞지 않으면
            throw new BaseException(MODIFY_FAIL_INVALID_USER_WISHLIST);
        }
        try{

            int result = postDao.modifyPostStatus(patchPostStatus);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_WISHLIST_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyPostImageStatus(PatchPostStatus patchPostStatus) throws BaseException {
        int checkUserId = postDao.checkUserId(patchPostStatus.getUserId());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        int checkPostImageStatus = postDao.checkPostImageStatus(patchPostStatus.getPostId());
        if(checkPostImageStatus == 0){//이미 삭제됐는지
            throw new BaseException(MODIFY_FAIL_INVALID_POSTIMAGE);
        }
        int checkUserPost = postDao.checkUserPost(patchPostStatus);
        if(checkUserPost == 0){//유저가 맞지 않으면
            throw new BaseException(MODIFY_FAIL_INVALID_USER_WISHLIST);
        }
        try{

            int result = postDao.modifyPostImageStatus(patchPostStatus);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_WISHLIST_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyOnePostImageStatus(PatchPostStatus patchPostStatus) throws BaseException {
        int checkUserId = postDao.checkUserId(patchPostStatus.getUserId());
        if(checkUserId == 0){//정상 상태가 아닌 유저라면
            throw new BaseException(POST_POST_INVALID_USER);
        }
        int checkOnePostImageStatus = postDao.checkOnePostImageStatus(patchPostStatus.getPostImageId());
        if(checkOnePostImageStatus == 0){//이미 삭제됐는지
            throw new BaseException(MODIFY_FAIL_INVALID_POSTIMAGE);
        }
        int checkUserPost = postDao.checkUserPost(patchPostStatus);
        if(checkUserPost == 0){//유저가 맞지 않으면
            throw new BaseException(MODIFY_FAIL_INVALID_USER_WISHLIST);
        }
        try{

            int result = postDao.modifyOnePostImageStatus(patchPostStatus);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_WISHLIST_STATUS);
            }
        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void patchPostComplete(int postId, int userIdByJwt, int buyerUserId) throws BaseException {
        //0. postId로 삭제된 post인지 확인
        //1. postId로 userId 조회
        //2. userId와 userIdByJwt가 같은 지 조회
        //3. postId가 dealComplete에 있는 지 조회
        //4. 없다면 추가 있다면 Valid
        //5. post에서 postId에 해당하는 status invalid 처리

        //0.
        if(postDao.checkPostDeleted(postId) == 1){
            throw new BaseException(MODIFY_FAIL_INVALID_POST);
        }
        //1.
        try{
            int userId = postDao.getUserIdByPostId(postId);
            //2.
            if(userId != userIdByJwt){
                throw new BaseException(INVALID_USER_JWT);
            }

            //3. dealcomplete에 이미 존재한다면
            if(postDao.checkDealcomplete(postId) == 1){
               //4
                postDao.patchDealcomplete(postId, userId, buyerUserId);
            }else{
                // dealcomplete에 없다면
                // 4
                postDao.postDealComplete(postId,userId,buyerUserId);
            }
            // 5
            postDao.patchPostStatusInvalid(postId, "Invalid");


        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void patchPostSale(int postId,int userIdByJwt) throws BaseException {
        //0. postId로 삭제된 post인지 확인
        //1. postId로 userId 조회
        //2. userId와 userIdByJwt가 같은 지 조회
        //3. postId가 dealComplete에 있는 지 조회
        //4. 있다면 Invalid
        //5. post에서 postId에 해당하는 status Valid 처리

        //0.
        if(postDao.checkPostDeleted(postId) == 1){
            throw new BaseException(MODIFY_FAIL_INVALID_POST);
        }
        //1.
        try{
            int userId = postDao.getUserIdByPostId(postId);
            //2.
            if(userId != userIdByJwt){
                throw new BaseException(INVALID_USER_JWT);
            }

            //3. dealcomplete에 이미 존재한다면
            if(postDao.checkDealcomplete(postId) == 1){
                //4
                postDao.patchDealcompleteSale(postId);
            }
            postDao.patchPostStatusInvalid(postId, "Valid");

        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public void patchPostReserved(int postId, int userIdByJwt, int bookerUserId) throws BaseException {
        //0. postId로 삭제된 post인지 확인
        //1. postId로 userId 조회
        //2. userId와 userIdByJwt가 같은 지 조회
        //3. postId가 dealComplete에 있는 지 조회
        //4. 없다면 추가 있다면 Reserved
        //5. post에서 postId에 해당하는 status Valid 처리

        //0.
        if(postDao.checkPostDeleted(postId) == 1){
            throw new BaseException(MODIFY_FAIL_INVALID_POST);
        }
        //1.
        try{
            int userId = postDao.getUserIdByPostId(postId);
            //2.
            if(userId != userIdByJwt){
                throw new BaseException(INVALID_USER_JWT);
            }

            //3. dealcomplete에 이미 존재한다면
            if(postDao.checkDealcomplete(postId) == 1){
                //4
                postDao.patchDealcompleteReserved(postId, userId, bookerUserId);
            }else{
                // dealcomplete에 없다면
                // 4
                postDao.postDealcompleteReserved(postId,userId,bookerUserId);
            }
            // 5
            postDao.patchPostStatusInvalid(postId, "Valid");


        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }



}

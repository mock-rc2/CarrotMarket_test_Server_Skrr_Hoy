package com.example.demo.src.post;

import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.PostService;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.wishList.model.PatchWishListStatus;
import com.example.demo.src.wishList.model.WishList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController
@RequestMapping("/post")
public class PostController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final PostProvider postProvider;
    @Autowired
    private final PostService postService;
    @Autowired
    private final JwtService jwtService;




    public PostController(PostProvider postProvider, PostService postService, JwtService jwtService){
        this.postProvider = postProvider;
        this.postService = postService;
        this.jwtService = jwtService;
    }

    /**
     * 특정 유저전체 판매중, 판매완료 게시물 조회 API
     * [GET] /post/:userId
     * @return BaseResponse<AllPostSelectRes>
     */
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<AllPostSelectRes>> allPostSelect(@PathVariable("userId") int userId){
        try{

            List<AllPostSelectRes> allPostSelectRes = postProvider.allPostSelect(userId);
            return new BaseResponse<>(allPostSelectRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 유저전체 판매중 게시물 조회 API
     * [GET] /post/sales/:userId
     * @return BaseResponse<AllPostSelectRes>
     */

    @ResponseBody
    @GetMapping("/{userId}/sales")
    public BaseResponse<List<AllPostSelectRes>> salePostSelect(@PathVariable("userId") int userId){
        try{

            List<AllPostSelectRes> salePostSelect = postProvider.salePostSelect(userId);
            return new BaseResponse<>(salePostSelect);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 유저전체 판매완료 게시물 조회 API
     * [GET] /post/deal-complete/:sellerUserId
     * @return BaseResponse<AllPostSelectRes>
     */

    @ResponseBody
    @GetMapping("/{sellerUserId}/sales/complete")
    public BaseResponse<List<AllPostSelectRes>> dealCompletePostSelect(@PathVariable("sellerUserId") int sellerUserId){
        try{

            List<AllPostSelectRes> dealCompletePostSelect = postProvider.dealCompletePostSelect(sellerUserId);
            return new BaseResponse<>(dealCompletePostSelect);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 유저의 판매 게시물 중 숨김 게시물 조회 API
     * [GET] /post/hide/:userId
     * @return BaseResponse<AllPostSelectRes>
     */

    @ResponseBody
    @GetMapping("/{userId}/hide")
    public BaseResponse<List<AllPostSelectRes>> hidePostSelect(@PathVariable("userId") int userId){
        try{

            List<AllPostSelectRes> hidePostSelect = postProvider.hidePostSelect(userId);
            return new BaseResponse<>(hidePostSelect);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 특정 유저전체 구매완료 게시물 조회 API
     * [GET] /post/deal-complete/:buyerUserId
     * @return BaseResponse<AllPostSelectRes>
     */

    @ResponseBody
    @GetMapping("/{buyerUserId}/purchase/complete")
    public BaseResponse<List<AllPostSelectRes>> purchaseCompletePostSelect(@PathVariable("buyerUserId") int buyerUserId){
        try{

            List<AllPostSelectRes> purchaseCompletePostSelect = postProvider.purchaseCompletePostSelect(buyerUserId);
            return new BaseResponse<>(purchaseCompletePostSelect);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 게시물 대표 이미지 조회 게시물(가장 먼저 들어간 이미지를 반환합니다.)
     * [GET] /post/title-image?postId={postId}
     * @return BaseResponse<AllPostSelectRes>
     */

    @ResponseBody
    @GetMapping("/title-image")
    public BaseResponse<GetPostImage> getPostTitleImage(@RequestParam("postId") int postId){
        try{
            GetPostImage getPostTitleImage = postProvider.getPostTitleImage(postId);
            return new BaseResponse(getPostTitleImage);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 게시물 이미지 조회 게시물(가장 먼저 들어간 이미지를 반환합니다.)
     * [GET] /post/title-image?postId={postId}
     * @return BaseResponse<AllPostSelectRes>
     */

    @ResponseBody
    @GetMapping("/image")
    public BaseResponse<List<GetPostImage>> getPostAllImage(@RequestParam("postId") int postId){
        try{
            List<GetPostImage> getPostAllImage = postProvider.getPostAllImage(postId);
            return new BaseResponse<>(getPostAllImage);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    //게시물 추가
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostPostRes> post(@RequestBody PostPostReq postPostReq){
        try{
            /*
            //휴대폰번호 입력 체크
            if(postPostReq.getUserId() == 0){
                return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
            }

             */

            PostPostRes postPostRes = postService.post(postPostReq);
            return new BaseResponse<>(postPostRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    //게시물 이미지 추가
    @ResponseBody
    @PostMapping("/image")
    public BaseResponse<PostPostImageRes> postImage(@RequestBody PostPostImageReq postPostImageReq){
        try{
            /*
            //휴대폰번호 입력 체크
            if(postPostReq.getUserId() == 0){
                return new BaseResponse<>(POST_USERS_EMPTY_PHONE);
            }

             */

            PostPostImageRes postPostImageRes = postService.postImage(postPostImageReq);
            return new BaseResponse<>(postPostImageRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<PostSelectRes>> getPostUseAddress(@RequestParam("townId") int townId, @RequestParam("range") int range, @RequestParam(required = false) String keyword, @RequestParam(required = false, defaultValue = "0") int categoryId){
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
            int userIdByJwt;
            userIdByJwt = jwtService.getUserId();
            if(keyword != null){
                if(categoryId != 0){
                    return new BaseResponse<>(GET_EXSIT_KEYWORD);
                }
                else{
                    List<PostSelectRes> getPostUseAddressByKeyword = postProvider.getPostUseAddressByKeyword(userIdByJwt, townId, range, keyword);
                    return new BaseResponse<>(getPostUseAddressByKeyword);
                }
            }

            else if(categoryId != 0){
                List<PostSelectRes> getPostUseAddressByCategory = postProvider.getPostUseAddressByCategory(userIdByJwt, townId, range, categoryId);
                return new BaseResponse<>(getPostUseAddressByCategory);
            }
            else{
                List<PostSelectRes> getPostUseAddress = postProvider.getPostUseAddress(userIdByJwt, townId, range);
                return new BaseResponse<>(getPostUseAddress);
            }

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @PatchMapping("/status/{postId}")
    public BaseResponse<String> modifyPostStatus(@PathVariable("postId") int postId, @RequestBody Post post){
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
            //jwt에서 idx 추출.
            int userId = jwtService.getUserId();

            if(post.getStatus().equals("Delete")){
                PatchPostStatus patchPostStatus = new PatchPostStatus(userId,postId,0,post.getStatus());
                postService.modifyPostStatus(patchPostStatus);

                String result = "";
                return new BaseResponse<>(result);
            }
            else{
                return new BaseResponse<>(PATCH_WISHLIST_INVALID_STATUS);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/image/{postId}/status")
    public BaseResponse<String> modifyPostImageStatus(@PathVariable("postId") int postId, @RequestParam(required = false, defaultValue = "-1") int postImageId, @RequestBody Post post){
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
            //jwt에서 idx 추출.
            int userId = jwtService.getUserId();

            if(post.getStatus().equals("Invalid")){
                if(postImageId != -1){
                    PatchPostStatus patchPostStatus = new PatchPostStatus(userId,postId,postImageId,post.getStatus());
                    postService.modifyOnePostImageStatus(patchPostStatus);
                }
                else{
                    PatchPostStatus patchPostStatus = new PatchPostStatus(userId,postId,postImageId,post.getStatus());
                    postService.modifyPostImageStatus(patchPostStatus);
                }


                String result = "";
                return new BaseResponse<>(result);
            }
            else{
                return new BaseResponse<>(PATCH_WISHLIST_INVALID_STATUS);
            }

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @PatchMapping("/{postId}/complete/{buyerUserId}")
    public BaseResponse<String> patchPostComplete(@PathVariable("postId") int postId, @PathVariable("buyerUserId") int buyerUserId) {
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
            int userId = jwtService.getUserId();
            postService.patchPostComplete(postId, userId, buyerUserId);

            String result = "";
            return new BaseResponse<>(result);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PatchMapping("/{postId}/sale")
    public BaseResponse<String> patchPostSale(@PathVariable("postId") int postId) {
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
            int userId = jwtService.getUserId();
            postService.patchPostSale(postId, userId);

            String result = "";
            return new BaseResponse<>(result);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }

    @ResponseBody
    @PatchMapping("/{postId}/reserved/{bookerUserId}")
    public BaseResponse<String> patchPostReserved(@PathVariable("postId") int postId, @PathVariable("bookerUserId") int bookerUserId) {
        //토큰 유효기간 파악
        try {
            Date current = new Date(System.currentTimeMillis());
            if(current.after(jwtService.getExp())){
                throw new BaseException(INVALID_JWT);
            }
            int userId = jwtService.getUserId();
            postService.patchPostReserved(postId, userId, bookerUserId);

            String result = "";
            return new BaseResponse<>(result);
        }catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }


}

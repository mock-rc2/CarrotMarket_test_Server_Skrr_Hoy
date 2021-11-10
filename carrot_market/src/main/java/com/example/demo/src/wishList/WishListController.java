package com.example.demo.src.wishList;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.wishList.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/wish-list")
public class WishListController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final WishListProvider wishListProvider;
    @Autowired
    private final WishListService wishListService;
    @Autowired
    private final JwtService jwtService;




    public WishListController(WishListProvider wishListProvider, WishListService wishListService, JwtService jwtService){
        this.wishListProvider = wishListProvider;
        this.wishListService = wishListService;
        this.jwtService = jwtService;
    }

    /**
     * 관심목록 조회 API
     * [GET] /wish-list
     * @return BaseResponse<WishListSelectRes>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<WishListSelectRes>> wishListSelect(){
        try{
            List<WishListSelectRes> wishListSelectRes = wishListProvider.wishListSelect();
            return new BaseResponse<>(wishListSelectRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }
    //게시물의 관심 목록 개수
    @ResponseBody
    @GetMapping("/count/{postId}")
    public BaseResponse<WishListCount> wishListCount(@PathVariable("postId") int postId){
        try{
            WishListCount wishListCount = wishListProvider.wishListCount(postId);
            return new BaseResponse(wishListCount);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    //관심목록 추가
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostWishListRes> postWishListRes(@RequestBody PostWishListReq postWishListReq){
        try{
            PostWishListRes postWishListRes = wishListService.createWishList(postWishListReq);
            return new BaseResponse(postWishListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    @ResponseBody
    @PatchMapping("/status/{wishListId}")
    public BaseResponse<String> modifyWishListStatus(@PathVariable("wishListId") int wishListId, @RequestBody WishList wishList){
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

            if(wishList.getStatus().equals("Invalid")){
                PatchWishListStatus patchWishListStatus = new PatchWishListStatus(userId,wishListId,wishList.getStatus());
                wishListService.modifyWishListStatus(patchWishListStatus);

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


}

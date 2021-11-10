package com.example.demo.src.category;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.address.model.GetNearTownListRes;
import com.example.demo.src.category.model.GetCategoryRes;
import com.example.demo.src.user.model.PatchUserProfileReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final CategoryProvider categoryProvider;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final JwtService jwtService;




    public CategoryController(CategoryProvider categoryProvider, CategoryService categoryService, JwtService jwtService){
        this.categoryProvider = categoryProvider;
        this.categoryService = categoryService;
        this.jwtService = jwtService;
    }

    /**
     * 모든 카테고리 조회
     * [GET] /category
     * @return BaseResponse<List<GetCategoryRes>>
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetCategoryRes>> getCategory() throws BaseException {

        try{
            List<GetCategoryRes> getCategoryRes  = categoryProvider.getCategory();
            return new BaseResponse<>(getCategoryRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저의 카테고리 조회
     * [GET] /category/:userId
     * @return BaseResponse<List<GetCategoryRes>>
     */
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetCategoryRes>> getUserCategory(@PathVariable("userId") int userId) throws BaseException {

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
            List<GetCategoryRes> getUserCategoryRes  = categoryProvider.getUserCategory(userId);
            return new BaseResponse<>(getUserCategoryRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 유저 카테고리 수정
     * [Patch] /category/:userId/:categoryId
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userId}/{categoryId}")
    public BaseResponse<String> patchUserCategory(@PathVariable("userId") int userId, @PathVariable("categoryId") int categoryId ){

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

            categoryService.patchUserCategory(userId, categoryId);

            String result = "카테고리 설정이 변경되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


}

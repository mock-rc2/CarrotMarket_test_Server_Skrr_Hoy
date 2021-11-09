package com.example.demo.src.category;


import com.example.demo.config.BaseException;
import com.example.demo.src.address.model.GetLocation;
import com.example.demo.src.address.model.GetTownRes;
import com.example.demo.src.category.model.GetCategoryRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.POST_POST_INVALID_USER;

//Provider : Read의 비즈니스 로직 처리
@Service
public class CategoryProvider {

    private final CategoryDao categoryDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public CategoryProvider(CategoryDao categoryDao, JwtService jwtService) {
        this.categoryDao = categoryDao;
        this.jwtService = jwtService;
    }

    public List<GetCategoryRes> getCategory() throws BaseException{

        try{
            List<GetCategoryRes> getCategoryRes = categoryDao.getCategory();
            return getCategoryRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public List<GetCategoryRes> getUserCategory(int userId) throws BaseException{

        try{
            List<GetCategoryRes> getUserCategoryRes = categoryDao.getUserCategory(userId);
            return getUserCategoryRes;
        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}

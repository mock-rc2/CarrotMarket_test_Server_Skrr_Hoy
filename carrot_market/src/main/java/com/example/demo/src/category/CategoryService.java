package com.example.demo.src.category;



import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class CategoryService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryDao categoryDao;
    private final CategoryProvider categoryProvider;
    private final JwtService jwtService;


    @Autowired
    public CategoryService(CategoryDao categoryDao, CategoryProvider categoryProvider, JwtService jwtService) {
        this.categoryDao = categoryDao;
        this.categoryProvider = categoryProvider;
        this.jwtService = jwtService;

    }

    public void patchUserCategory(int userId,int categoryId) throws BaseException{

        try {
            categoryDao.patchUserCategory(userId, categoryId);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}

package com.example.demo.src.category;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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




}

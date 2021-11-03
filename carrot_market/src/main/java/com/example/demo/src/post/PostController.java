package com.example.demo.src.post;

import com.example.demo.src.post.PostProvider;
import com.example.demo.src.post.PostService;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.post.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
     * @return BaseResponse<PostLoginRes>
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

}

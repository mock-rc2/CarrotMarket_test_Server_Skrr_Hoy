package com.example.demo.src.post;



import com.example.demo.config.BaseException;
import com.example.demo.src.post.model.PostPostImageReq;
import com.example.demo.src.post.model.PostPostImageRes;
import com.example.demo.src.post.model.PostPostReq;
import com.example.demo.src.post.model.PostPostRes;
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



}

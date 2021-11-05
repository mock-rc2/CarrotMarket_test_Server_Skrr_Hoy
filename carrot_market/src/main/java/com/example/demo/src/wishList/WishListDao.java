package com.example.demo.src.wishList;


import com.example.demo.src.wishList.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class WishListDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //특정유저 전체 판매중, 판매완료 게시물 조회
    public List<WishListSelectRes> wishListSelect(int userId){
        String getWishListSelectResQuery = "select W.wishListId, P.postId, P.townId, P.title, P.categoryId, P.cost, P.content, P.status\n" +
                "from WishList W\n" +
                "left join Post P on P.postId = W.postId\n" +
                "where W.userId = ? && W.status = 'Valid' && (P.status = 'Valid' || P.status = 'Invalid');";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getWishListSelectResQuery,
                (rs,rowNum) -> new WishListSelectRes(
                        rs.getInt("wishListId"),
                        rs.getInt("postId"),
                        rs.getInt("townId"),
                        rs.getString("title"),
                        rs.getInt("categoryId"),
                        rs.getInt("cost"),
                        rs.getString("content"),
                        rs.getString("status")),
                userId
        );
    }

    //유저아이디로 유저가 존재하는지
    public int checkUserId(int userId){
        String checkUserIdQuery = "select exists(select phoneNumber from User where userId = ? && status='Valid')";
        int checkUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserIdQuery,
                int.class,
                checkUserIdParams);

    }

    public int checkPostId(int postId){
        String checkPostIdQuery = "select exists(select * from Post where postId = ? && status != 'Delete')";
        int checkPostIdParams = postId;
        return this.jdbcTemplate.queryForObject(checkPostIdQuery,
                int.class,
                checkPostIdParams);

    }

    //특정 게시물 관심목록 개수 조회
    public WishListCount wishListCount(int postId){
        String getgetWishListCountQuery = "select count(*) as cnt from WishList where postId = ? && status = 'Valid'";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.queryForObject(getgetWishListCountQuery,
                (rs,rowNum) -> new WishListCount(
                        postId,
                        rs.getInt("cnt")),
                postId
        );
    }

    //특정 게시물 관심목록 추가
    public int createWishList(PostWishListReq postWishListReq){
        String createWishListQuery = "insert into WishList (userId,postId) VALUES (?,?)";    //디비에 이 쿼리를 날린다.
        Object[] createWishListParams = new Object[]{postWishListReq.getUserId(), postWishListReq.getPostId()};
        this.jdbcTemplate.update(createWishListQuery, createWishListParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    //상태 수정
    public int modifyWishListStatus(PatchWishListStatus patchWishListStatus){
        String modifyUserStatusQuery = "update WishList set status = ? where wishListId = ? ";
        Object[] modifyUserStatusParams = new Object[]{patchWishListStatus.getStatus(), patchWishListStatus.getWishListId()};

        return this.jdbcTemplate.update(modifyUserStatusQuery,modifyUserStatusParams);
    }
    //상태조회
    public int checkStatus(int wishListId){
        String checkWishListQuery = "select exists(select * from WishList where wishListId = ? && status='Valid')";
        int checkWishListParams = wishListId;
        return this.jdbcTemplate.queryForObject(checkWishListQuery,
                int.class,
                checkWishListParams);

    }
    //유저의 관심목록이 맞는지 체크
    public int checkUserWish(PatchWishListStatus patchWishListStatus){
        String checkUserWishQuery = "select exists(select * from WishList where userId = ? && wishListId = ?)";
        Object[] checkUserWishParams = new Object[]{patchWishListStatus.getUserId(), patchWishListStatus.getWishListId()};
        return this.jdbcTemplate.queryForObject(checkUserWishQuery,
                int.class,
                checkUserWishParams);

    }


}

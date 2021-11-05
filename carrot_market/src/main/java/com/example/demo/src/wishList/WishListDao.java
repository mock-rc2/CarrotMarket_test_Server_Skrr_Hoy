package com.example.demo.src.wishList;


import com.example.demo.src.user.model.PostUserReq;
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
        String getWishListSelectResQuery = "select P.postId, P.townId, P.title, P.categoryId, P.cost, P.content, P.status\n" +
                "from WishList W\n" +
                "left join Post P on P.postId = W.postId\n" +
                "where W.userId = ? && W.status = 'Valid' && (P.status = 'Valid' || P.status = 'Invalid');";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getWishListSelectResQuery,
                (rs,rowNum) -> new WishListSelectRes(
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

    /*


    //특정 유저 판매중 게시물 조회
    public List<AllWishListSelectRes> saleWishListSelect(int userId){
        String getsaleWishListSelectQuery = "select postId, townId, title, categoryId, cost, content, status\n" +
                "from Post\n" +
                "where userId = ? && status = 'Valid'\n" +
                "ORDER BY created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getsaleWishListSelectQuery,
                (rs,rowNum) -> new AllWishListSelectRes(
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

    //특정 유저 판매완료 게시물 조회
    public List<AllWishListSelectRes> dealCompleteWishListSelect(int sellerUserId){
        String getdealCompleteWishListSelectQuery = "select P.postId, P.townId, P.title, P.categoryId, P.cost, P.content, P.status\n" +
                "from Post P\n" +
                "left join DealComplete DC on P.userId = DC.sellerUserId && P.postId = DC.postId\n" +
                "where P.userId = ? && (P.status = 'Invalid' && DC.status = 'Valid')\n" +
                "ORDER BY P.created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getdealCompleteWishListSelectQuery,
                (rs,rowNum) -> new AllWishListSelectRes(
                        rs.getInt("postId"),
                        rs.getInt("townId"),
                        rs.getString("title"),
                        rs.getInt("categoryId"),
                        rs.getInt("cost"),
                        rs.getString("content"),
                        rs.getString("status")),
                sellerUserId
        );
    }

    //특정 유저 판매중 게시물 조회
    public List<AllWishListSelectRes> hideWishListSelect(int userId){
        String gethideWishListSelectQuery = "select postId, townId, title, categoryId, cost, content, status\n" +
                "from Post\n" +
                "where userId = ? && status = 'Hide'\n" +
                "ORDER BY created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(gethideWishListSelectQuery,
                (rs,rowNum) -> new AllWishListSelectRes(
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

    //특정 유저 구매완료 게시물 조회
    public List<AllWishListSelectRes> purchaseCompleteWishListSelect(int buyerUserId){
        String getpurchaseCompleteWishListSelectQuery = "select P.postId, P.townId, P.title, P.categoryId, P.cost, P.content, P.status\n" +
                "from Post P\n" +
                "left join DealComplete DC on P.postId = DC.postId\n" +
                "where DC.buyerUserId = ? && (P.status = 'Invalid' && DC.status = 'Valid')\n" +
                "ORDER BY P.created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getpurchaseCompleteWishListSelectQuery,
                (rs,rowNum) -> new AllWishListSelectRes(
                        rs.getInt("postId"),
                        rs.getInt("townId"),
                        rs.getString("title"),
                        rs.getInt("categoryId"),
                        rs.getInt("cost"),
                        rs.getString("content"),
                        rs.getString("status")),
                buyerUserId
        );
    }
    //특정 게시물 대표 이미지 조회
    public GetWishListImage getWishListTitleImage(int postId){
        String getgetWishListTitleImageQuery = "select postImageId, image\n" +
                "from PostImage\n" +
                "where postId = 13 && status = 'Valid'\n" +
                "order by postImageId asc,created asc limit 1;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.queryForObject(getgetWishListTitleImageQuery,
                (rs,rowNum) -> new GetWishListImage(
                        rs.getInt("postImageId"),
                        rs.getString("image")),
                postId
        );
    }

    //특정 게시물 전체 이미지 조회
    public List<GetWishListImage> getWishListAllImage(int postId){
        String getWishListAllImageQuery = "select postImageId, image\n" +
                "from PostImage\n" +
                "where postId = ? && status = 'Valid'\n" +
                "order by postImageId asc,created asc";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getWishListAllImageQuery,
                (rs,rowNum) -> new GetWishListImage(
                        rs.getInt("postImageId"),
                        rs.getString("image")),
                postId
        );
    }

     */


}

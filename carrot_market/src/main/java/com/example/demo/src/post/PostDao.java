package com.example.demo.src.post;


import com.example.demo.src.post.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PostDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //특정유저 전체 판매중, 판매완료 게시물 조회
    public List<AllPostSelectRes> allPostSelect(int userId){
        String getAllPostSelectResQuery = "select P.postId, P.townId, P.title, P.categoryId, P.cost, P.content, P.status\n" +
                "from Post P\n" +
                "left join DealComplete DC on P.userId = DC.sellerUserId && P.postId = DC.postId\n" +
                "where P.userId = ? && (P.status = 'Valid' || (P.status = 'Invalid' && DC.status = 'Valid'))\n" +
                "ORDER BY P.created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getAllPostSelectResQuery,
                (rs,rowNum) -> new AllPostSelectRes(
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

    //특정 유저 판매중 게시물 조회
    public List<AllPostSelectRes> salePostSelect(int userId){
        String getsalePostSelectQuery = "select postId, townId, title, categoryId, cost, content, status\n" +
                "from Post\n" +
                "where userId = ? && status = 'Valid'\n" +
                "ORDER BY created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getsalePostSelectQuery,
                (rs,rowNum) -> new AllPostSelectRes(
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
    public List<AllPostSelectRes> dealCompletePostSelect(int sellerUserId){
        String getdealCompletePostSelectQuery = "select P.postId, P.townId, P.title, P.categoryId, P.cost, P.content, P.status\n" +
                "from Post P\n" +
                "left join DealComplete DC on P.userId = DC.sellerUserId && P.postId = DC.postId\n" +
                "where P.userId = ? && (P.status = 'Invalid' && DC.status = 'Valid')\n" +
                "ORDER BY P.created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getdealCompletePostSelectQuery,
                (rs,rowNum) -> new AllPostSelectRes(
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

    public List<AllPostSelectRes> purchaseCompletePostSelect(int buyerUserId){
        String getpurchaseCompletePostSelectQuery = "select P.postId, P.townId, P.title, P.categoryId, P.cost, P.content, P.status\n" +
                "from Post P\n" +
                "left join DealComplete DC on P.postId = DC.postId\n" +
                "where DC.buyerUserId = ? && (P.status = 'Invalid' && DC.status = 'Valid')\n" +
                "ORDER BY P.created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getpurchaseCompletePostSelectQuery,
                (rs,rowNum) -> new AllPostSelectRes(
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


}

package com.example.demo.src.chatting;


import com.example.demo.src.chatting.model.*;
import com.example.demo.src.post.model.GetPostImage;
import com.example.demo.src.post.model.PatchPostStatus;
import com.example.demo.src.post.model.PostPostReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChattingDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //유저아이디로 유저가 존재하는지
    public int checkUserId(int userId){
        String checkUserIdQuery = "select exists(select phoneNumber from User where userId = ? && status='Valid')";
        int checkUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserIdQuery,
                int.class,
                checkUserIdParams);

    }

    //게시물 아이디로 게시물 존재하는지
    public int checkPostId(int postId){
        String checkPostIdQuery = "select exists(select * from Post where postId = ? && status != 'Delete')";
        int checkPostIdParams = postId;
        return this.jdbcTemplate.queryForObject(checkPostIdQuery,
                int.class,
                checkPostIdParams);

    }

    //특정 게시물 전체 채팅 조회
    public List<GetChattingRoom> allChattingSelect(int userId){
        String getChattingRoomQuery = "select chattingRoomId, case when\n" +
                "sellerUserId = ? then buyerUserId when buyerUserId = ? then sellerUserId end as otherUserId,\n" +
                "       postId from ChattingRoom\n" +
                "where (sellerUserId = ? OR buyerUserId = ?) AND status = 'Valid';";    //디비에 이 쿼리를 날린다.
        Object[] getChattingRoomParams = new Object[]{userId,userId,userId,userId};
        return this.jdbcTemplate.query(getChattingRoomQuery,
                (rs,rowNum) -> new GetChattingRoom(
                        rs.getInt("ChattingRoomId"),
                        rs.getInt("otherUserId"),
                        rs.getInt("postId")),
                getChattingRoomParams
        );
    }

    public PostChattingCountRes postChattingCount(int postId){
        String getPostChattingCountResQuery = "select count(*) as count from ChattingRoom where postId = ?;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.queryForObject(getPostChattingCountResQuery,
                (rs,rowNum) -> new PostChattingCountRes(
                        rs.getInt("count")
                        ),
                postId
        );
    }

    public GetChattingContent getChattingContent(int chattingRoomId){
        String getChattingContentQuery = "select chattingContentId, userId, content, case when\n" +
                "                TIMESTAMPDIFF(YEAR, created, current_timestamp) >= 1\n" +
                "               then DATE_FORMAT(created, \"%Y년 %m월 %d일\")# 1년 이상은 년도 표시\n" +
                "                else DATE_FORMAT(created, \"%m월 %d일\") end as time,\n" +
                "       CASE WHEN\n" +
                "        INSTR(DATE_FORMAT(created, '%p %h:%i'), 'PM') > 0\n" +
                "        THEN\n" +
                "        REPLACE(DATE_FORMAT(created, '%p %h:%i'), 'PM', '오후')\n" +
                "        ELSE\n" +
                "        REPLACE(DATE_FORMAT(created, '%p %h:%i'), 'AM', '오전')\n" +
                "        END AS day\n" +
                "\n" +
                "from ChattingContent where chattingRoomId = ? AND status = 'Valid' order by created DESC limit 1;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.queryForObject(getChattingContentQuery,
                (rs,rowNum) -> new GetChattingContent(
                        rs.getInt("chattingContentId"),
                        rs.getInt("userId"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getString("day")
                ),
                chattingRoomId
        );
    }

    public List<GetChattingContent> getAllChattingContent(int chattingRoomId){
        String getChattingRoomQuery = "select chattingContentId, userId, content, case when\n" +
                "                TIMESTAMPDIFF(YEAR, created, current_timestamp) >= 1\n" +
                "               then DATE_FORMAT(created, \"%Y년 %m월 %d일\")# 1년 이상은 년도 표시\n" +
                "                else DATE_FORMAT(created, \"%m월 %d일\") end as time,\n" +
                "       CASE WHEN\n" +
                "        INSTR(DATE_FORMAT(created, '%p %h:%i'), 'PM') > 0\n" +
                "        THEN\n" +
                "        REPLACE(DATE_FORMAT(created, '%p %h:%i'), 'PM', '오후')\n" +
                "        ELSE\n" +
                "        REPLACE(DATE_FORMAT(created, '%p %h:%i'), 'AM', '오전')\n" +
                "        END AS day\n" +
                "\n" +
                "from ChattingContent where chattingRoomId = ? AND status = 'Valid' order by created ASC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getChattingRoomQuery,
                (rs,rowNum) -> new GetChattingContent(
                        rs.getInt("chattingContentId"),
                        rs.getInt("userId"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getString("day")),
                chattingRoomId
        );
    }

    public int createChattingRoom(int sellerUserId,PostChattingReq postChattingReq){
        String createChattingRoomQuery = "insert into ChattingRoom (sellerUserId, buyerUserId, postId) VALUES (?,?,?)";
        Object[] createChattingRoomParams = new Object[]{sellerUserId,postChattingReq.getBuyerUserId(),postChattingReq.getPostId()};
        this.jdbcTemplate.update(createChattingRoomQuery, createChattingRoomParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    //상태 수정
    public int modifyChattingRoomStatus(PatchChattingRoomStatus patchChattingRoomStatus){
        String modifyPostStatusQuery = "update ChattingRoom set status = ? where chattingRoomId = ? ";
        Object[] modifyPostStatusParams = new Object[]{patchChattingRoomStatus.getStatus(), patchChattingRoomStatus.getChattingRoomId()};

        return this.jdbcTemplate.update(modifyPostStatusQuery,modifyPostStatusParams);
    }

    //채팅 보내
    public int createChattingContent(int chattingRoomId, int userId, PostChattingContentReq postChattingContentReq){
        String createChattingContentQuery = "insert into ChattingContent (chattingRoomId, userId, content) VALUES (?,?,?)";
        Object[] createChattingContentParams = new Object[]{chattingRoomId,userId,postChattingContentReq.getContent()};
        this.jdbcTemplate.update(createChattingContentQuery, createChattingContentParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }




}

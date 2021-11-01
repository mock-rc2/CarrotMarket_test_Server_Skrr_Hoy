package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //휴대폰 번호로 유저 조회
    public User getUsersByPhoneNumber(PostLoginReq postLoginReq){
        String getUsersByPhoneNumberQuery = "select * from User where phoneNumber = ?";
        String getUsersByPhoneNumberParams = postLoginReq.getPhoneNumber();
        return this.jdbcTemplate.queryForObject(getUsersByPhoneNumberQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userId"),
                        rs.getString("phoneNumber"),
                        rs.getString("nickName"),
                        rs.getString("email"),
                        rs.getString("imageURL")),
                getUsersByPhoneNumberParams);
    }


    //유저 상태 조회 쿼리
    public int checkStatus(String phoneNumber){
        String checkStatusQuery = "select exists(select phoneNumber from User where phoneNumber = ? && status='Valid')";
        String checkStatusParams = phoneNumber;
        return this.jdbcTemplate.queryForObject(checkStatusQuery,
                int.class,
                checkStatusParams);

    }




}

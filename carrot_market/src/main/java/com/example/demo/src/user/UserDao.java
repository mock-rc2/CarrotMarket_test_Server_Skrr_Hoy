package com.example.demo.src.user;


import com.example.demo.src.address.model.GetAddressRes;
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
                        rs.getString("image")),
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

    //유저 인증번호 일치 여부 조회 쿼리
    public int checkCertificationNum(PostLoginReq postLoginReq){
        String checkCertificationNumQuery = "select exists(select phoneNumber from User where phoneNumber = ? && certificationNum= ?)";
        Object[] checkCertificationNumParams = new Object[]{postLoginReq.getPhoneNumber(), postLoginReq.getCertificationNum()};
        return this.jdbcTemplate.queryForObject(checkCertificationNumQuery,
                int.class,
                checkCertificationNumParams);

    }


    public int getTownId(PostUserReq postUserReq){
        String getTownIdQuery = "select townId from Town where city = ? and district = ? and townName = ?";
        return this.jdbcTemplate.queryForObject(getTownIdQuery,
                int.class,
                postUserReq.getCity(),postUserReq.getDistrict(), postUserReq.getTownName());
    }


    public int createUser(PostUserReq postUserReq, String certificationNum){
        String createUserQuery = "insert into User (phoneNumber, nickName,certificationNum, image) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{postUserReq.getPhoneNumber(), postUserReq.getNickName(),certificationNum, postUserReq.getImage()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public void createAddress(int userId,int townId){
        String createAddressQuery = "insert into Address (userId,townId) VALUES (?,?)";
        Object[] createAddressParams = new Object[]{userId, townId};
        this.jdbcTemplate.update(createAddressQuery, createAddressParams);

    }

    public int checkPhoneNumber(String phoneNumber){
        String checkStatusQuery = "select exists(select phoneNumber from User where phoneNumber = ? && status='Valid')";
        String checkStatusParams = phoneNumber;
        return this.jdbcTemplate.queryForObject(checkStatusQuery,
                int.class,
                checkStatusParams);
    }


    public int checkUserExist(int userId) {
        String checkUserExistQuery = "select exists(select userId from User where userId = ? and status='Valid')";
        int checkUserExistParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserExistQuery,
                int.class,
                checkUserExistParams);
    }

    public GetUserRes getUser(int userId){
        String getUserquery = "select nickName, image from User where userId = ? ";
        return this.jdbcTemplate.queryForObject(getUserquery,
                (rs, rowNum) -> new GetUserRes(
                        userId,
                        rs.getString("image"),
                        rs.getString("nickName")
                ),
                userId);
    }

    public void patchUserProfile(int userId, PatchUserProfileReq patchUserProfileReq){
        String patchUserProfileQuery = "update User set nickName = ?,nickNameUpdated = CURRENT_TIMESTAMP, image = ? where userId = ? ";
        Object[] patchUserProfileParams = new Object[]{patchUserProfileReq.getNickName(),patchUserProfileReq.getImage(),userId};

        this.jdbcTemplate.update(patchUserProfileQuery,patchUserProfileParams);
    }

    public void patchUserProfileImage(int userId, String image){
        String patchUserProfileQuery = "update User set image = ? where userId = ? ";
        Object[] patchUserProfileParams = new Object[]{image ,userId};

        this.jdbcTemplate.update(patchUserProfileQuery,patchUserProfileParams);

    }

    public String checkUserNickName(int userId){
        String checkUserNickNameQuery = "select nickName from User where userId = ? ";
        int checkUserNickNameParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserNickNameQuery,
                String.class,
                checkUserNickNameParams);
    }


    public int checkNickNameUpdated(int userId){
        String checkNickNameUpdatedQuery = "select DATEDIFF(CURDATE(), COALESCE(nickNameUpdated, DATE_ADD(CURDATE(), INTERVAL -31 DAY))) from User where userId = ?";
        int checkNickNameUpdatedParams = userId;
        return this.jdbcTemplate.queryForObject(checkNickNameUpdatedQuery,
                int.class,
                checkNickNameUpdatedParams);
    }
    public GetUserAccountRes getUserAccount(int userId){
        String getUserAccountuery = "select phoneNumber, email from User where userId = ? ";
        return this.jdbcTemplate.queryForObject(getUserAccountuery,
                (rs, rowNum) -> new GetUserAccountRes(
                        rs.getString("email"),
                        rs.getString("phoneNumber")
                ),
                userId);
    }

    public void createCategory(int userId){
        for (int i = 1; i <= 18; i++) {
            String createCategoryQuery = "insert into InterestCategory (userId, categoryId) values (?, ?) ";
            Object[] createCategoryParams = new Object[]{userId, i};

            this.jdbcTemplate.update(createCategoryQuery, createCategoryParams);
        }
    }

    public int checkUserId(int userId){
        String checkUserIdQuery = "select exists(select phoneNumber from User where userId = ? && status='Valid')";
        int checkUserIdParams = userId;
        return this.jdbcTemplate.queryForObject(checkUserIdQuery,
                int.class,
                checkUserIdParams);

    }

    void saveCertificationInfo(String phoneNumber, String certificationNum) {
        String saveCertificationInfoQuery = "insert into Oauth (phoneNumber, certificationNum) values (?, ?) ";
        Object[] saveCertificationInfoParams = new Object[]{phoneNumber, certificationNum};
        this.jdbcTemplate.update(saveCertificationInfoQuery, saveCertificationInfoParams);
    }

    //유저 인증번호 일치 여부 조회 쿼리-Oauth
    public int checkCertificationNumByOauth(PostLoginReq postLoginReq){
        String checkCertificationNumQuery = "select exists(select phoneNumber from Oauth where phoneNumber = ? && certificationNum= ?)";
        Object[] checkCertificationNumParams = new Object[]{postLoginReq.getPhoneNumber(), postLoginReq.getCertificationNum()};
        return this.jdbcTemplate.queryForObject(checkCertificationNumQuery,
                int.class,
                checkCertificationNumParams);

    }


}

package com.example.demo.src.category;


import com.example.demo.src.address.model.GetLocation;
import com.example.demo.src.category.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetCategoryRes> getCategory(){
        String getCategoryQuery = "select categoryId,categoryName, image  from Category";
        ArrayList<GetCategoryRes> list = new ArrayList<>();

        this.jdbcTemplate.query(getCategoryQuery,
                (rs, rowNum) -> list.add(new GetCategoryRes(rs.getInt("categoryId"),rs.getString("categoryName"), rs.getString("image"))));

        return list;
    }

    public List<GetCategoryRes> getUserCategory(int userId){
        String getUserCategoryQuery = "select c.categoryId, categoryName, image from Category as c\n" +
                "inner join InterestCategory as ic on c.categoryId = ic.categoryId\n" +
                "where ic.userId = ? and ic.status = 'Valid' ";
        ArrayList<GetCategoryRes> list = new ArrayList<>();

        this.jdbcTemplate.query(getUserCategoryQuery,
                (rs, rowNum) -> list.add(new GetCategoryRes(rs.getInt("categoryId"),rs.getString("categoryName"), rs.getString("image"))), userId);

        return list;
    }

    public void patchUserCategory(int userId,int categoryId){
        String patchUserCategoryQuery = "update InterestCategory set status = if(status ='Valid', 'Invalid', 'Valid') where userId = ? and categoryId = ? ";
        Object[] patchUserCategoryParams = new Object[]{userId, categoryId};

        this.jdbcTemplate.update(patchUserCategoryQuery, patchUserCategoryParams);
    }
}

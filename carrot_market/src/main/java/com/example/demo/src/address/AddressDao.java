package com.example.demo.src.address;


import com.example.demo.src.address.model.*;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AddressDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetTownRes> getTownOrderByAddress(String search,Double lat,Double lng){
        String getTownOrderByAddressQuery = "select city,\n" +
                "       district,\n" +
                "       townName,\n" +
                "       case\n" +
                "           when T.townId in (select townId from TownEtc where etc is not null)\n" +
                "               then group_concat(etc separator ', ')\n" +
                "           else NULL\n" +
                "           end as etc\n" +
                "\n" +
                "from Town as T\n" +
                "         left outer join TownEtc TE on T.townId = TE.townId\n" +
                "where city like concat('%', ?, '%')\n" +
                "   or district like concat('%', ?, '%')\n" +
                "   or townName like concat('%', ?, '%')\n" +
                "   or T.townId in (select townId from TownEtc where etc like concat('%', ?, '%'))\n" +
                "group by city, district, townName, lat, lng\n" +
                "order by (6371 * acos(cos(radians(?)) * cos(radians(lat)) * cos(radians(lng) - radians(?)) +\n" +
                "                      sin(radians(?)) * sin(radians(lat))))";
        return this.jdbcTemplate.query(getTownOrderByAddressQuery,
                (rs, rowNum) -> new GetTownRes(
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("townName"),
                        rs.getString("etc")
                ),

                search,search,search,search,lat,lng,lat);
    }

    public List<GetTownRes> getTownOrderByName(String search){
        String getTownOrderByNameQuery = "select city,\n" +
                "       district,\n" +
                "       townName,\n" +
                "       case\n" +
                "           when T.townId in (select townId from TownEtc where etc is not null)\n" +
                "               then group_concat(etc separator ', ')\n" +
                "           else NULL\n" +
                "           end as etc\n" +
                "\n" +
                "from Town as T\n" +
                "         left outer join TownEtc TE on T.townId = TE.townId\n" +
                "where city like concat('%', ?, '%')\n" +
                "   or district like concat('%', ?, '%')\n" +
                "   or townName like concat('%', ?, '%')\n" +
                "   or T.townId in (select townId from TownEtc where etc like concat('%', ?, '%'))\n" +
                "group by city, district, townName\n" +
                "order by city, district, townName";
        return this.jdbcTemplate.query(getTownOrderByNameQuery,
                (rs, rowNum) -> new GetTownRes(
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("townName"),
                        rs.getString("etc")
                ),

                search,search,search,search);
    }

    public GetLocation getLocation(int townId){
        String getLocationQuery = "select lat, lng from Town where townId = ? ";
        return this.jdbcTemplate.queryForObject(getLocationQuery,
                (rs, rowNum) -> new GetLocation(
                        rs.getDouble("lat"),
                        rs.getDouble("lng")
                ),
                townId);
    }

    public List<GetTownRes> getNearTownOrderByName(Double lat, Double lng){
        String getNearTownOrderByName = "select T.townId,city,\n" +
                "       district,\n" +
                "       townName,\n" +
                "       case\n" +
                "           when T.townId in (select townId from TownEtc where etc is not null)\n" +
                "               then group_concat(etc separator ', ')\n" +
                "           else NULL\n" +
                "           end as etc\n" +
                "\n" +
                "from Town as T\n" +
                "         left outer join TownEtc TE on T.townId = TE.townId\n" +
                "\n" +
                "group by city, district, townName, lat, lng\n" +
                "order by (6371 * acos(cos(radians(?)) * cos(radians(lat)) * cos(radians(lng) - radians(?)) +\n" +
                "                      sin(radians(?)) * sin(radians(lat))))";

        return this.jdbcTemplate.query(getNearTownOrderByName,
                (rs, rowNum) -> new GetTownRes(
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("townName"),
                        rs.getString("etc")
                ),
                lat,lng,lat);
    }

    public int getTownId(String city,String district,String townName){
        String gettownIdQuery = "select townId from Town where city = ? and district = ? and townName = ? ";
        return this.jdbcTemplate.queryForObject(gettownIdQuery,
                int.class,
                city, district, townName
        );
    }


    public ArrayList<Integer> getNearTownListByRange(int range, Double lat, Double lng){
        String getNearTownIdQuery = "SELECT townId\n" +
                "FROM Town\n" +
                "where ((6371 * acos(cos(radians(?)) * cos(radians(lat)) *\n" +
                "                   cos(radians(lng) - radians(?)) +\n" +
                "                   sin(radians(?)) * sin(radians(lat)))) < ?)";

        ArrayList<Integer> list = new ArrayList<>();

        this.jdbcTemplate.query(getNearTownIdQuery,
                (rs, rowNum) -> list.add(rs.getInt("townId")),
                lat,lng,lat, range);
        return list;

    }


    public int getIsExistAddress(int userId,int townId){
        String getIsExistAddressQuery = "select exists( select addressId from Address where userId = ? and townId = ?)";
        return this.jdbcTemplate.queryForObject(getIsExistAddressQuery,
                int.class,
                userId, townId
        );
    }

    public int getAddressId(int userId,int townId){
        String getAddressIdQuery = "select addressId from Address where userId = ? and townId = ?";
        return this.jdbcTemplate.queryForObject(getAddressIdQuery,
                int.class,
                userId, townId
        );
    }

    public void patchAddressStatusValid(int addressId){

        String modifyLoginPasswordQuery = "update Address set status = 'Valid', selectAddress = 'Valid' where addressId = ?";
        Object[] modifyLoginPasswordParams = new Object[]{addressId};

        this.jdbcTemplate.update(modifyLoginPasswordQuery,modifyLoginPasswordParams);

    }

    public void createAddress(int userId, int townId){
        String createAddressQuery = "insert into Address (userId, townId) VALUES (?,?)";
        Object[] createUserParams = new Object[]{userId, townId};
        this.jdbcTemplate.update(createAddressQuery, createUserParams);
    }

    public int countUserAddress(int userId){
        String countUserAddressQuery = "select count(*) from Address where userId = ? and status = 'Valid'";
        return this.jdbcTemplate.queryForObject(countUserAddressQuery,
                int.class,
                userId
        );
    }

    public int isSelectedTown(int userId, int townId){
        String getIsSelectedTownQuery = "select exists( select addressId from Address where userId = ? and townId = ? and status = 'Valid')";
        return this.jdbcTemplate.queryForObject(getIsSelectedTownQuery,
                int.class,
                userId, townId
        );

    }
    public void patchAddressStatusInvalid(int addressId){
        String patchAddressStatusInvalidQuery = "update Address set status = 'Invalid', selectAddress ='Invalid' where addressId = ?";
        Object[] patchAddressStatusInvalidParams = new Object[]{addressId};

        this.jdbcTemplate.update(patchAddressStatusInvalidQuery,patchAddressStatusInvalidParams);
    }

    public void patchSelectAddress(int userId){
        String patchSelectAddressQuery = "update Address set selectAddress = 'Invalid' where userId = ? and selectAddress = 'Valid'";
        Object[] patchSelectAddressParams = new Object[]{userId};

        this.jdbcTemplate.update(patchSelectAddressQuery,patchSelectAddressParams);
    }


    public int getAddressIdByState(int userId){
        String getAddressIdByStateQuery = "select addressId from Address where userId = ?  and status = 'Valid'";
        return this.jdbcTemplate.queryForObject(getAddressIdByStateQuery,
                int.class,
                userId
        );
    }


}

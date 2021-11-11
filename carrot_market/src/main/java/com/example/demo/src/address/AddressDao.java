package com.example.demo.src.address;


import com.example.demo.src.address.model.*;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class AddressDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public List<GetTownRes> getTownOrderByAddress(String[] search,Double lat,Double lng){
        String q0 = "select  a1.city,\n" +
                "       a1.district,\n" +
                "       a1.townName, a1.etc\n" +
                "from\n";
        String q1 = "(select city,\n" +
                "       district,\n" +
                "       townName,\n" +
                "       case\n" +
                "           when T.townId in (select townId from TownEtc where etc is not null)\n" +
                "               then group_concat(etc separator ', ')\n" +
                "           else NULL\n" +
                "           end as etc,\n" +
                "lat, lng\n"+
                "\n" +
                "from Town as T\n" +
                "         left outer join TownEtc TE on T.townId = TE.townId\n" +
                "where city like concat('%','";
        String q2 = "', '%')\n" +
                "   or district like concat('%','";
        String q3 =  "', '%')\n" +
                "   or townName like concat('%','";
        String q4 =  "', '%')\n" +
                "   or T.townId in (select townId from TownEtc where etc like concat('%','";
        String q5 = "', '%'))\n" +
                "group by city, district, townName , lat,lng \n";


        String order = "order by (6371 * acos(cos(radians("+lat+")) * cos(radians(lat)) * cos(radians(lng) - radians("+lng+")) +\n" +
                " sin(radians("+lat+")) * sin(radians(lat)))))\n";

        String getTownOrderByAddressQuery = q0 +  q1 + search[0] + q2 + search[0] + q3 + search[0] + q4 + search[0] + q5 + order;
        getTownOrderByAddressQuery += " as a1\n";

        for(int i = 1; i < search.length; i++) {
            getTownOrderByAddressQuery += "inner join  " + q1 + search[i] + q2 + search[i] + q3 + search[i] + q4 + search[i] + q5 + order
                    + "as a" + (i + 1) + " on a" + i + ".city = a" + (i + 1) + ".city and a" + i + ".district = a" + (i + 1) + ".district and a" + i + ".townName = a" + (i + 1) + ".townName\n";
        }




        return this.jdbcTemplate.query(getTownOrderByAddressQuery,
                (rs, rowNum) -> new GetTownRes(
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("townName"),
                        rs.getString("etc")
                ));
    }

    public List<GetTownRes> getTownOrderByName(String[] search){
        String q0 = "select  a1.city,\n" +
                "       a1.district,\n" +
                "       a1.townName, a1.etc\n" +
                "from\n";
        String q1 = "(select city,\n" +
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
                "where city like concat('%','";
        String q2 = "', '%')\n" +
                "   or district like concat('%','";
        String q3 =  "', '%')\n" +
                "   or townName like concat('%','";
        String q4 =  "', '%')\n" +
                "   or T.townId in (select townId from TownEtc where etc like concat('%','";
        String q5 = "', '%'))\n" +
                "group by city, district, townName\n)";

        String order = "order by city, district, townName\n";

        String getTownOrderByNameQuery = q0 +  q1 + search[0] + q2 + search[0] + q3 + search[0] + q4 + search[0] + q5;
        getTownOrderByNameQuery += " as a1\n";

        for(int i = 1; i < search.length; i++) {
                getTownOrderByNameQuery += "inner join  " + q1 + search[1] + q2 + search[1] + q3 + search[1] + q4 + search[1] + q5
                        + "as a"+(i+1) +" on a"+i+".city = a"+(i+1)+".city and a"+i+".district = a"+(i+1)+".district and a"+i+".townName = a"+(i+1)+".townName\n";
        }
        getTownOrderByNameQuery += order;


        return this.jdbcTemplate.query(getTownOrderByNameQuery,
                        (rs, rowNum) -> new GetTownRes(
                                rs.getString("city"),
                                rs.getString("district"),
                                rs.getString("townName"),
                                rs.getString("etc")
                        ));


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

        String modifyLoginPasswordQuery = "update Address set status = 'Valid', mainTown = 'Valid' where addressId = ?";
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
        String patchAddressStatusInvalidQuery = "update Address set status = 'Invalid', mainTown ='Invalid' where addressId = ?";
        Object[] patchAddressStatusInvalidParams = new Object[]{addressId};

        this.jdbcTemplate.update(patchAddressStatusInvalidQuery,patchAddressStatusInvalidParams);
    }

    public void patchmainTown(int userId){
        String patchmainTownQuery = "update Address set mainTown = 'Invalid' where userId = ? and mainTown = 'Valid'";
        Object[] patchmainTownParams = new Object[]{userId};

        this.jdbcTemplate.update(patchmainTownQuery,patchmainTownParams);
    }


    public int getAddressIdByState(int userId){
        String getAddressIdByStateQuery = "select addressId from Address where userId = ?  and status = 'Valid'";
        return this.jdbcTemplate.queryForObject(getAddressIdByStateQuery,
                int.class,
                userId
        );
    }


    public GetAddressRes getAddress(int userId){
        String getAddressQuery = "select townId, certification, `range` from Address where userId = ? and mainTown = 'Valid'  ";
        return this.jdbcTemplate.queryForObject(getAddressQuery,
                (rs, rowNum) -> new GetAddressRes(
                    rs.getInt("townId"),
                    rs.getString("certification"),
                    rs.getInt("range")
                ),
                userId);
    }

    public GetTownNameRes getTownName(int townId){
        String getTownNameQuery = "select city, district, townName from Town where townId = ?";
        return this.jdbcTemplate.queryForObject(getTownNameQuery,
                (rs, rowNum) -> new GetTownNameRes(
                        rs.getString("city"),
                        rs.getString("district"),
                        rs.getString("townName")
                ),
                townId);
    }


    public void patchAddressRange(int addressId,int range){
        String patchAddressRangeQuery = "update Address set `range` = ? where addressId = ? ";
        Object[] patchAddressRangeParams = new Object[]{range, addressId};

        this.jdbcTemplate.update(patchAddressRangeQuery,patchAddressRangeParams);
    }

    public ArrayList<Integer> getUserAddress(int userId){
        String getUserAddressQuery = "select townId from Address where userId = ? and status ='Valid' ";
        ArrayList<Integer> list = new ArrayList<>();

        this.jdbcTemplate.query(getUserAddressQuery,
                (rs, rowNum) -> list.add(rs.getInt("townId")),
                userId);
        return list;
    }


    public int getDateDiffCertification(int addressId){
        String getDateDiffCertificationQuery = "select DATEDIFF(CURDATE(), certificationUpdated) from Address where addressId = ? ";
        return this.jdbcTemplate.queryForObject(getDateDiffCertificationQuery,
                int.class,
                addressId
        );
    }

    public void patchCertificationInvalid(int addressId){
        // 1. certification Invalid 로 바꾸기
        String patchCertificationInvalidQuery = "update Address set certification = 'Invalid' where addressId = ? ";
        Object[] patchCertificationInvalidParams = new Object[]{addressId};

        this.jdbcTemplate.update(patchCertificationInvalidQuery,patchCertificationInvalidParams);

        //2. update시간 바꾸기
        String patchCertificatioUpdatedQuery = "update Address set certificationUpdated = CURRENT_TIMESTAMP where addressId = ? ";
        Object[] patchCertificatioUpdatedParams = new Object[]{addressId};

        this.jdbcTemplate.update(patchCertificatioUpdatedQuery,patchCertificatioUpdatedParams);

   }

    public int isSelectedAddress(int userId, int townId) {
        String getIsSelectedTownQuery = "select exists( select addressId from Address where userId = ? and townId = ? and mainTown = 'Valid')";
        return this.jdbcTemplate.queryForObject(getIsSelectedTownQuery,
                int.class,
                userId, townId
        );
    }

    public void patchCertificationAddress(int addressId){
        String patchCertificationAddressQuery = "update Address set certification = 'Valid' where addressId = ? ";
        Object[] patchCertificationAddressParams = new Object[]{addressId};

        this.jdbcTemplate.update(patchCertificationAddressQuery,patchCertificationAddressParams);

        //2. update시간 바꾸기
        String patchCertificatioUpdatedQuery = "update Address set certificationUpdated = CURRENT_TIMESTAMP where addressId = ? ";
        Object[] patchCertificatioUpdatedParams = new Object[]{addressId};

        this.jdbcTemplate.update(patchCertificatioUpdatedQuery,patchCertificatioUpdatedParams);

    }

}





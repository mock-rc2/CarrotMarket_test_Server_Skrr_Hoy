package com.example.demo.src.post;


import com.example.demo.src.post.model.*;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.wishList.model.PatchWishListStatus;
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

    //게시물 아이디로 게시물 존재하는지
    public int checkPostId(int postId){
        String checkPostIdQuery = "select exists(select * from Post where postId = ? && status != 'Delete')";
        int checkPostIdParams = postId;
        return this.jdbcTemplate.queryForObject(checkPostIdQuery,
                int.class,
                checkPostIdParams);

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

    //특정 유저 판매중 게시물 조회
    public List<AllPostSelectRes> hidePostSelect(int userId){
        String gethidePostSelectQuery = "select postId, townId, title, categoryId, cost, content, status\n" +
                "from Post\n" +
                "where userId = ? && status = 'Hide'\n" +
                "ORDER BY created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(gethidePostSelectQuery,
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

    //특정 유저 구매완료 게시물 조회
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
    //특정 게시물 대표 이미지 조회
    public GetPostImage getPostTitleImage(int postId){
        String getgetPostTitleImageQuery = "select postImageId, image\n" +
                "from PostImage\n" +
                "where postId = ? && status = 'Valid'\n" +
                "order by postImageId asc,created asc limit 1;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.queryForObject(getgetPostTitleImageQuery,
                (rs,rowNum) -> new GetPostImage(
                        rs.getInt("postImageId"),
                        rs.getString("image")),
                postId
        );
    }

    //특정 게시물 전체 이미지 조회
    public List<GetPostImage> getPostAllImage(int postId){
        String getPostAllImageQuery = "select postImageId, image\n" +
                "from PostImage\n" +
                "where postId = ? && status = 'Valid'\n" +
                "order by postImageId asc,created asc";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getPostAllImageQuery,
                (rs,rowNum) -> new GetPostImage(
                        rs.getInt("postImageId"),
                        rs.getString("image")),
                postId
        );
    }

    public int createPost(PostPostReq postPostReq){
        String createPostQuery = "insert into Post (userId, townId, title, categoryId, cost, content) VALUES (?,?,?,?,?,?)";
        Object[] createPostParams = new Object[]{postPostReq.getUserId(), postPostReq.getTownId(), postPostReq.getTitle(), postPostReq.getCategoryId(),postPostReq.getCost(),postPostReq.getContent()};
        this.jdbcTemplate.update(createPostQuery, createPostParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public int createPostImage(PostPostImageReq postPostImageReq){
        String createPostImageQuery = "insert into PostImage (postId, image) VALUES (?,?)";
        Object[] createPostImageParams = new Object[]{postPostImageReq.getPostId(), postPostImageReq.getImage()};
        this.jdbcTemplate.update(createPostImageQuery, createPostImageParams);
        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public List<PostSelectRes> getPostUseAddress(String selectPostQurey, String interestCategoryQurey){
        String getPostUseAddressQuery = "select P.postId, P.userId, T.townName, P.title, P.categoryId, P.cost, P.content, case" +
                "           when TIMESTAMPDIFF(second, P.created, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(second, P.created, current_timestamp), '초 전') # 1분 미만에는 초로 표시\n" +
                "           when TIMESTAMPDIFF(minute, P.created, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(minute, P.created, current_timestamp), '분 전') # 1시간 미만에는 분으로 표시\n" +
                "           when TIMESTAMPDIFF(hour, P.created, current_timestamp) < 24\n" +
                "               then concat(TIMESTAMPDIFF(hour, P.created, current_timestamp), '시간 전') # 24시간 미만에는 시간으로 표시\n" +
                "           when TIMESTAMPDIFF(DAY, P.created, current_timestamp) < 31\n" +
                "               then concat(TIMESTAMPDIFF(DAY, P.created, current_timestamp), '일 전') # 31일 미만에는 일로 표시\n" +
                "            when TIMESTAMPDIFF(MONTH, P.created, current_timestamp) < 12\n" +
                "               then concat(TIMESTAMPDIFF(MONTH, P.created, current_timestamp), '개월 전') # 12월 미만에는 월로 표시\n" +
                "           when TIMESTAMPDIFF(YEAR, P.created, current_timestamp) >= 1\n" +
                "               then concat(TIMESTAMPDIFF(YEAR, P.created, current_timestamp), '년 전') # 1년 이상은 년으로 표시\n" +
                "\n" +
                "           end as time, P.status from Post P left join (select townId, townName from Town) as T on P.townId=T.townId where P.townId IN ("+ selectPostQurey +") AND P.categoryId IN (" + interestCategoryQurey + ") AND (P.status = 'Valid' OR P.status = 'Invalid') ORDER BY P.created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getPostUseAddressQuery,
                (rs,rowNum) -> new PostSelectRes(
                        rs.getInt("postId"),
                        rs.getInt("userId"),
                        rs.getString("townName"),
                        rs.getString("title"),
                        rs.getInt("categoryId"),
                        rs.getInt("cost"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getString("status"))
        );
    }

    public List<PostSelectRes> getPostUseAddressByKeyword(String selectPostQurey, String keyword){
        String getPostUseAddressQuery = "select P.postId, P.userId, T.townName, P.title, P.categoryId, P.cost, P.content, case" +
                "           when TIMESTAMPDIFF(second, P.created, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(second, P.created, current_timestamp), '초 전') # 1분 미만에는 초로 표시\n" +
                "           when TIMESTAMPDIFF(minute, P.created, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(minute, P.created, current_timestamp), '분 전') # 1시간 미만에는 분으로 표시\n" +
                "           when TIMESTAMPDIFF(hour, P.created, current_timestamp) < 24\n" +
                "               then concat(TIMESTAMPDIFF(hour, P.created, current_timestamp), '시간 전') # 24시간 미만에는 시간으로 표시\n" +
                "           when TIMESTAMPDIFF(DAY, P.created, current_timestamp) < 31\n" +
                "               then concat(TIMESTAMPDIFF(DAY, P.created, current_timestamp), '일 전') # 31일 미만에는 일로 표시\n" +
                "            when TIMESTAMPDIFF(MONTH, P.created, current_timestamp) < 12\n" +
                "               then concat(TIMESTAMPDIFF(MONTH, P.created, current_timestamp), '개월 전') # 12월 미만에는 월로 표시\n" +
                "           when TIMESTAMPDIFF(YEAR, P.created, current_timestamp) >= 1\n" +
                "               then concat(TIMESTAMPDIFF(YEAR, P.created, current_timestamp), '년 전') # 1년 이상은 년으로 표시\n" +
                "\n" +
                "           end as time, P.status from Post P left join (select townId, townName from Town) as T on P.townId=T.townId where P.townId IN ("+ selectPostQurey +") AND (P.status = 'Valid' OR P.status = 'Invalid') AND P.title like concat('%', ?, '%') ORDER BY P.created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getPostUseAddressQuery,
                (rs,rowNum) -> new PostSelectRes(
                        rs.getInt("postId"),
                        rs.getInt("userId"),
                        rs.getString("townName"),
                        rs.getString("title"),
                        rs.getInt("categoryId"),
                        rs.getInt("cost"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getString("status")),
                keyword
        );
    }

    public List<PostSelectRes> getPostUseAddressByCategory(String selectPostQurey, int categoryId){
        String getPostUseAddressQuery = "select P.postId, P.userId, T.townName, P.title, P.categoryId, P.cost, P.content, case" +
                "           when TIMESTAMPDIFF(second, P.created, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(second, P.created, current_timestamp), '초 전') # 1분 미만에는 초로 표시\n" +
                "           when TIMESTAMPDIFF(minute, P.created, current_timestamp) < 60\n" +
                "               then concat(TIMESTAMPDIFF(minute, P.created, current_timestamp), '분 전') # 1시간 미만에는 분으로 표시\n" +
                "           when TIMESTAMPDIFF(hour, P.created, current_timestamp) < 24\n" +
                "               then concat(TIMESTAMPDIFF(hour, P.created, current_timestamp), '시간 전') # 24시간 미만에는 시간으로 표시\n" +
                "           when TIMESTAMPDIFF(DAY, P.created, current_timestamp) < 31\n" +
                "               then concat(TIMESTAMPDIFF(DAY, P.created, current_timestamp), '일 전') # 31일 미만에는 일로 표시\n" +
                "            when TIMESTAMPDIFF(MONTH, P.created, current_timestamp) < 12\n" +
                "               then concat(TIMESTAMPDIFF(MONTH, P.created, current_timestamp), '개월 전') # 12월 미만에는 월로 표시\n" +
                "           when TIMESTAMPDIFF(YEAR, P.created, current_timestamp) >= 1\n" +
                "               then concat(TIMESTAMPDIFF(YEAR, P.created, current_timestamp), '년 전') # 1년 이상은 년으로 표시\n" +
                "\n" +
                "           end as time, status from Post P left join (select townId, townName from Town) as T on P.townId=T.townId where categoryId = ? AND P.townId IN ("+ selectPostQurey +") AND (status = 'Valid' OR status = 'Invalid') ORDER BY created DESC;";    //디비에 이 쿼리를 날린다.
        return this.jdbcTemplate.query(getPostUseAddressQuery,
                (rs,rowNum) -> new PostSelectRes(
                        rs.getInt("postId"),
                        rs.getInt("userId"),
                        rs.getString("townName"),
                        rs.getString("title"),
                        rs.getInt("categoryId"),
                        rs.getInt("cost"),
                        rs.getString("content"),
                        rs.getString("time"),
                        rs.getString("status")),
                categoryId
        );
    }

    //상태 수정
    public int modifyPostStatus(PatchPostStatus patchPostStatus){
        String modifyPostStatusQuery = "update Post set status = ? where postId = ? ";
        Object[] modifyPostStatusParams = new Object[]{patchPostStatus.getStatus(), patchPostStatus.getPostId()};

        return this.jdbcTemplate.update(modifyPostStatusQuery,modifyPostStatusParams);
    }
    //상태조회
    public int checkPostStatus(int postId){
        String checkPostQuery = "select exists(select * from Post where postId = ? && status!='Delete')";
        int checkPostParams = postId;
        return this.jdbcTemplate.queryForObject(checkPostQuery,
                int.class,
                checkPostParams);

    }
    //유저의 관심목록이 맞는지 체크
    public int checkUserPost(PatchPostStatus patchPostStatus){
        String checkUserPostQuery = "select exists(select * from Post where userId = ? && postId = ?)";
        Object[] checkUserPostParams = new Object[]{patchPostStatus.getUserId(), patchPostStatus.getPostId()};
        return this.jdbcTemplate.queryForObject(checkUserPostQuery,
                int.class,
                checkUserPostParams);

    }

    //게시글 전체 이미지 삭제
    public int modifyPostImageStatus(PatchPostStatus patchPostStatus){
        String modifyPostImageStatusQuery = "update PostImage set status = ? where postId = ? ";
        Object[] modifyPostImageStatusParams = new Object[]{patchPostStatus.getStatus(), patchPostStatus.getPostId()};

        return this.jdbcTemplate.update(modifyPostImageStatusQuery,modifyPostImageStatusParams);
    }
    //게시글 특정 이미지 삭제
    public int modifyOnePostImageStatus(PatchPostStatus patchPostStatus){
        String modifyOnePostImageStatusQuery = "update PostImage set status = ? where postImageId = ? ";
        Object[] modifyOnePostImageStatusParams = new Object[]{patchPostStatus.getStatus(), patchPostStatus.getPostImageId()};

        return this.jdbcTemplate.update(modifyOnePostImageStatusQuery,modifyOnePostImageStatusParams);
    }
    //게시글 인덱스로 이미지 존재하는지
    public int checkPostImageStatus(int postId){
        String checkPostImageQuery = "select exists(select * from PostImage where postId = ? && status='Valid')";
        int checkPostImageParams = postId;
        return this.jdbcTemplate.queryForObject(checkPostImageQuery,
                int.class,
                checkPostImageParams);

    }

    //게시글 이미지 인덱스로 특정 이미지 존재하는지
    public int checkOnePostImageStatus(int postImageId){
        String checkOnePostImageQuery = "select exists(select * from PostImage where postImageId = ? && status='Valid')";
        int checkOnePostImageParams = postImageId;
        return this.jdbcTemplate.queryForObject(checkOnePostImageQuery,
                int.class,
                checkOnePostImageParams);

    }



    public int checkPostDeleted(int postId){
        String checkPostDeletedQuery = "select exists(select * from Post where postId = ? && status='Delete')";
        int checkPostDeletedParams = postId;
        return this.jdbcTemplate.queryForObject(checkPostDeletedQuery,
                int.class,
                checkPostDeletedParams);
    }
    public int getUserIdByPostId(int postId){
        String getUserIdByPostIdQuery = "select userId from Post where postId = ?";
        int getUserIdByPostIdParams = postId;
        return this.jdbcTemplate.queryForObject(getUserIdByPostIdQuery,
                int.class,
                getUserIdByPostIdParams);
    }

    public int checkDealcomplete(int postId){
        String checkDealcompleteQuery = "select exists(select * from DealComplete where postId = ?)";
        int checkDealcompleteParams = postId;
        return this.jdbcTemplate.queryForObject(checkDealcompleteQuery,
                int.class,
                checkDealcompleteParams);
    }

    public void patchDealcomplete(int postId,int userId,int buyerUserId){
        String patchDealcompleteQuery = "update DealComplete set status = 'Valid', sellerUserId = ?, buyerUserId = ? where postId = ? ";
        Object[] patchDealcompleteParams = new Object[]{userId, buyerUserId, postId };

        this.jdbcTemplate.update(patchDealcompleteQuery,patchDealcompleteParams);
    }

    public void postDealComplete(int postId,int userId,int buyerUserId){
        String postDealCompleteQuery = "insert into DealComplete (postId, sellerUserId, buyerUserId, status) VALUES (?,?,?, 'Valid')";
        Object[] postDealCompleteParams = new Object[]{postId, userId, buyerUserId};
        this.jdbcTemplate.update(postDealCompleteQuery, postDealCompleteParams);

    }

    public void patchPostStatusInvalid(int postId, String status){
        String patchPostStatusInvalidQuery = "update Post set status = ? where postId = ?";
        Object[] patchPostStatusInvalidParams = new Object[]{ status, postId };

        this.jdbcTemplate.update(patchPostStatusInvalidQuery,patchPostStatusInvalidParams);
    }

    public void patchDealcompleteSale(int postId){
        String patchDealcompleteSaleQuery = "update DealComplete set status = 'Invalid' where postId = ? ";
        Object[] patchDealcompleteSaleParams = new Object[]{ postId };

        this.jdbcTemplate.update(patchDealcompleteSaleQuery, patchDealcompleteSaleParams );

    }



    public void patchDealcompleteReserved(int postId,int userId,int bookerUserId){
        String patchDealcompleteReservedQuery = "update DealComplete set status = 'Reserved', sellerUserId = ?, bookerUserId = ? where postId = ? ";
        Object[] patchDealcompleteReservedParams = new Object[]{userId, bookerUserId, postId };

        this.jdbcTemplate.update(patchDealcompleteReservedQuery,patchDealcompleteReservedParams);
    }


    public void postDealcompleteReserved(int postId,int userId,int bookerUserId){
        String postDealCompleteQuery = "insert into DealComplete (postId, sellerUserId, bookerUserId, status) VALUES (?,?,?, 'Reserved')";
        Object[] postDealCompleteParams = new Object[]{postId, userId, bookerUserId};
        this.jdbcTemplate.update(postDealCompleteQuery, postDealCompleteParams);
    }
}

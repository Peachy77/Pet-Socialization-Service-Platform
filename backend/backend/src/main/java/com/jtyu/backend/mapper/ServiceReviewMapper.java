package com.jtyu.backend.mapper;

import com.jtyu.backend.model.ServiceReview;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceReviewMapper {
    // ========== 评论查询 ==========

    @Select("SELECT sr.review_id, sr.user_id, sr.service_id, sr.rating, sr.content, sr.images, sr.create_time, " +
            "u.username, u.avatar " +
            "FROM service_comment sr JOIN user u ON sr.user_id = u.user_id " +
            "WHERE sr.service_id = #{serviceId} ORDER BY sr.create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectByServiceId(@Param("serviceId") Integer serviceId,
                                                @Param("offset") Integer offset,
                                                @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM service_comment WHERE service_id = #{serviceId}")
    Long countByServiceId(@Param("serviceId") Integer serviceId);

    @Select("SELECT AVG(rating) as avgRating, COUNT(*) as count FROM service_comment WHERE service_id = #{serviceId}")
    Map<String, Object> selectRatingStats(@Param("serviceId") Integer serviceId);

    @Select("SELECT sr.review_id, sr.user_id, sr.service_id, sr.rating, sr.content, sr.images, sr.create_time, " +
            "u.username, u.avatar " +
            "FROM service_comment sr JOIN user u ON sr.user_id = u.user_id " +
            "WHERE sr.review_id = #{reviewId}")
    Map<String, Object> selectById(@Param("reviewId") Integer reviewId);

    // ========== 评论操作 ==========

    @Insert("INSERT INTO service_comment (user_id, service_id, rating, content, images) " +
            "VALUES (#{userId}, #{serviceId}, #{rating}, #{content}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "reviewId")
    int insert(ServiceReview review);

    @Delete("DELETE FROM service_comment WHERE review_id = #{reviewId}")
    int deleteById(@Param("reviewId") Integer reviewId);

    // 检查评论是否存在
    @Select("SELECT COUNT(*) FROM service_comment WHERE review_id = #{reviewId}")
    int existsById(@Param("reviewId") Integer reviewId);

    // 检查是否是自己的评论
    @Select("SELECT user_id FROM service_comment WHERE review_id = #{reviewId}")
    Integer selectUserIdByReviewId(@Param("reviewId") Integer reviewId);
    // ServiceReviewMapper.java 中添加

    // 获取商户评论的回复列表
    @Select("SELECT sr.review_id, sr.user_id, sr.service_id, sr.rating, sr.content, sr.images, sr.create_time, " +
            "u.username, u.avatar " +
            "FROM service_comment sr JOIN user u ON sr.user_id = u.user_id " +
            "WHERE sr.parent_review_id = #{parentReviewId} ORDER BY sr.create_time ASC")
    List<Map<String, Object>> selectRepliesByParentId(@Param("parentReviewId") Integer parentReviewId);
}

package com.jtyu.backend.mapper;

import com.jtyu.backend.model.ServiceReview;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceReviewMapper {

    // ========== 根评论查询 ==========

    @Select("SELECT sr.review_id, sr.user_id, sr.service_id, sr.rating, sr.content, sr.images, sr.create_time, " +
            "u.username, u.avatar, " +
            "(SELECT COUNT(*) > 0 FROM service_review_like WHERE review_id = sr.review_id AND user_id = #{currentUserId}) as liked, " +
            "(SELECT COUNT(*) FROM service_review_like WHERE review_id = sr.review_id) as like_count " +
            "FROM service_comment sr JOIN user u ON sr.user_id = u.user_id " +
            "WHERE sr.service_id = #{serviceId} AND sr.parent_review_id = 0 " +
            "ORDER BY sr.create_time DESC LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectRootByServiceId(@Param("serviceId") Integer serviceId,
                                                    @Param("offset") Integer offset,
                                                    @Param("pageSize") Integer pageSize,
                                                    @Param("currentUserId") Integer currentUserId);

    @Select("SELECT COUNT(*) FROM service_comment WHERE service_id = #{serviceId} AND parent_review_id = 0")
    Long countRootByServiceId(@Param("serviceId") Integer serviceId);

    // ========== 回复查询 ==========

    @Select("SELECT sr.review_id, sr.user_id, sr.service_id, sr.content, sr.images, sr.create_time, " +
            "u.username, u.avatar, " +
            "(SELECT COUNT(*) > 0 FROM service_review_like WHERE review_id = sr.review_id AND user_id = #{currentUserId}) as liked, " +
            "(SELECT COUNT(*) FROM service_review_like WHERE review_id = sr.review_id) as like_count " +
            "FROM service_comment sr JOIN user u ON sr.user_id = u.user_id " +
            "WHERE sr.parent_review_id = #{reviewId} " +
            "ORDER BY sr.create_time ASC")
    List<Map<String, Object>> selectRepliesByParentId(@Param("reviewId") Integer reviewId,
                                                      @Param("currentUserId") Integer currentUserId);

    // ========== 单条查询 ==========

    @Select("SELECT sr.review_id, sr.user_id, sr.service_id, sr.rating, sr.content, sr.images, sr.parent_review_id, sr.create_time " +
            "FROM service_comment sr WHERE sr.review_id = #{reviewId}")
    Map<String, Object> selectById(@Param("reviewId") Integer reviewId);
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

    // ========== 评论操作 ==========

    @Insert("INSERT INTO service_comment (user_id, service_id, rating, content, images, parent_review_id) " +
            "VALUES (#{userId}, #{serviceId}, #{rating}, #{content}, #{images}, #{parentReviewId})")
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

}

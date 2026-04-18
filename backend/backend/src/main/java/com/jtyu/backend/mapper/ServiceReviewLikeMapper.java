package com.jtyu.backend.mapper;


import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceReviewLikeMapper {

    @Select("SELECT COUNT(*) FROM service_review_like WHERE user_id = #{userId} AND review_id = #{reviewId}")
    int exists(@Param("userId") Integer userId, @Param("reviewId") Integer reviewId);

    @Insert("INSERT INTO service_review_like (user_id, review_id) VALUES (#{userId}, #{reviewId})")
    int insert(@Param("userId") Integer userId, @Param("reviewId") Integer reviewId);

    @Delete("DELETE FROM service_review_like WHERE user_id = #{userId} AND review_id = #{reviewId}")
    int delete(@Param("userId") Integer userId, @Param("reviewId") Integer reviewId);

    @Select("SELECT COUNT(*) FROM service_review_like WHERE review_id = #{reviewId}")
    int countByReviewId(@Param("reviewId") Integer reviewId);
    // ServiceReviewMapper.java 中添加

    // 获取商户评论的回复列表
    @Select("SELECT sr.review_id, sr.user_id, sr.service_id, sr.rating, sr.content, sr.images, sr.create_time, " +
            "u.username, u.avatar " +
            "FROM service_comment sr JOIN user u ON sr.user_id = u.user_id " +
            "WHERE sr.parent_review_id = #{parentReviewId} ORDER BY sr.create_time ASC")
    List<Map<String, Object>> selectRepliesByParentId(@Param("parentReviewId") Integer parentReviewId);
}

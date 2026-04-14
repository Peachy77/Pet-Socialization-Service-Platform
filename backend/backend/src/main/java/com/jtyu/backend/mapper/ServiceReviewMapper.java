package com.jtyu.backend.mapper;

import com.jtyu.backend.model.ServiceReview;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ServiceReviewMapper {

    @Select("SELECT * FROM service_comment WHERE service_id = #{serviceId} ORDER BY create_time DESC")
    List<ServiceReview> findByServiceId(@Param("serviceId") Integer serviceId);

    @Select("SELECT * FROM service_comment WHERE review_id = #{reviewId}")
    ServiceReview findById(@Param("reviewId") Integer reviewId);

    @Insert("INSERT INTO service_comment (user_id, service_id, rating, content, images) " +
            "VALUES (#{userId}, #{serviceId}, #{rating}, #{content}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "reviewId")
    int insert(ServiceReview ServiceReview);

    @Update("UPDATE service_comment SET rating = #{rating}, content = #{content}, images = #{images} WHERE review_id = #{reviewId}")
    int update(ServiceReview ServiceReview);

    @Delete("DELETE FROM service_comment WHERE review_id = #{reviewId}")
    int deleteById(@Param("reviewId") Integer reviewId);

    @Delete("DELETE FROM service_comment WHERE service_id = #{serviceId}")
    int deleteByServiceId(@Param("serviceId") Integer serviceId);

    @Select("SELECT COALESCE(AVG(rating), 0) FROM service_comment WHERE service_id = #{serviceId}")
    double getAverageRating(@Param("serviceId") Integer serviceId);
}

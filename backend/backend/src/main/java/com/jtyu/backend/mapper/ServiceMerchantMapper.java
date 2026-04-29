package com.jtyu.backend.mapper;

import com.jtyu.backend.model.ServiceMerchant;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface ServiceMerchantMapper {
    // ========== 商户查询 ==========

    @Select("SELECT  service_id as serviceId, name, category, address, images, phone, rating, review_count as reviewCount, description, services_offered " +
            "FROM service WHERE service_id = #{serviceId}")
    Map<String, Object> selectById(@Param("serviceId") Integer serviceId);

    @Select("<script>" +
            "SELECT service_id, name, category, address, images, phone, rating, review_count, description " +
            "FROM service WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR address LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            " ORDER BY rating DESC LIMIT #{offset}, #{pageSize}" +
            "</script>")
    List<Map<String, Object>> selectList(@Param("keyword") String keyword,
                                         @Param("category") String category,
                                         @Param("offset") Integer offset,
                                         @Param("pageSize") Integer pageSize);

    @Select("<script>" +
            "SELECT COUNT(*) FROM service WHERE 1=1 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') OR address LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))" +
            "</if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            "</script>")
    Long countList(@Param("keyword") String keyword, @Param("category") String category);



    // ========== 评分更新 ==========

    @Update("UPDATE service SET rating = #{rating}, review_count = #{reviewCount} WHERE service_id = #{serviceId}")
    int updateRating(@Param("serviceId") Integer serviceId,
                     @Param("rating") BigDecimal rating,
                     @Param("reviewCount") Integer reviewCount);

    // 检查商户是否存在
    @Select("SELECT COUNT(*) FROM service WHERE service_id = #{serviceId}")
    int existsById(@Param("serviceId") Integer serviceId);

}

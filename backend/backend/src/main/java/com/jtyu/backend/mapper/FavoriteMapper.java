package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface FavoriteMapper {
    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId} AND service_id = #{serviceId}")
    int exists(@Param("userId") Integer userId, @Param("serviceId") Integer serviceId);

    @Insert("INSERT INTO favorite (user_id, service_id) VALUES (#{userId}, #{serviceId})")
    int insert(@Param("userId") Integer userId, @Param("serviceId") Integer serviceId);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND service_id = #{serviceId}")
    int delete(@Param("userId") Integer userId, @Param("serviceId") Integer serviceId);

    // 获取用户收藏列表（带商户信息）
    @Select("SELECT f.favorite_id, f.user_id, f.service_id, f.create_time, " +
            "s.name, s.address, s.images, s.rating, s.category, " +
            "COALESCE(t.min_price, 0) as min_price " +
            "FROM favorite f " +
            "JOIN service s ON f.service_id = s.service_id " +
            "LEFT JOIN ( " +
            "  SELECT service_id, " +
            "         MIN(CAST(JSON_EXTRACT(value, '$.price') AS DECIMAL(10,2))) AS min_price " +
            "  FROM service, " +
            "       JSON_TABLE(services_offered, '$[*]' COLUMNS(value JSON PATH '$')) AS jt " +
            "  GROUP BY service_id " +
            ") t ON s.service_id = t.service_id " +
            "WHERE f.user_id = #{userId} " +
            "ORDER BY f.create_time DESC " +
            "LIMIT #{offset}, #{pageSize}")
    List<Map<String, Object>> selectFavoritesWithService(@Param("userId") Integer userId,
                                                         @Param("offset") Integer offset,
                                                         @Param("pageSize") Integer pageSize);

    @Select("SELECT COUNT(*) FROM favorite WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Integer userId);
}

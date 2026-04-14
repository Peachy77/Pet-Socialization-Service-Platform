package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Service;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ServiceMapper {
    @Select("SELECT * FROM service ORDER BY rating DESC")
    List<Service> findAll();

    @Select("SELECT * FROM service WHERE category = #{category} ORDER BY rating DESC")
    List<Service> findByCategory(@Param("category") String category);

    @Select("SELECT * FROM service WHERE service_id = #{serviceId}")
    Service findById(@Param("serviceId") Integer serviceId);

    @Insert("INSERT INTO service (name, category, address, images, phone, business_hours, description, services_offered) " +
            "VALUES (#{name}, #{category}, #{address}, #{images}, #{phone}, #{businessHours}, #{description}, #{servicesOffered})")
    @Options(useGeneratedKeys = true, keyProperty = "serviceId")
    int insert(Service service);

    @Update("UPDATE service SET name = #{name}, category = #{category}, address = #{address}, " +
            "images = #{images}, phone = #{phone}, business_hours = #{businessHours}, " +
            "description = #{description}, services_offered = #{servicesOffered} WHERE service_id = #{serviceId}")
    int update(Service service);
    @Delete("DELETE FROM service WHERE service_id = #{serviceId}")
    int deleteById(@Param("serviceId") Integer serviceId);

    @Update("UPDATE service SET rating = (" +
            "SELECT COALESCE(AVG(rating), 0) FROM service_comment WHERE service_id = #{serviceId}" +
            "), review_count = (" +
            "SELECT COUNT(*) FROM service_comment WHERE service_id = #{serviceId}" +
            ") WHERE service_id = #{serviceId}")
    int updateRating(@Param("serviceId") Integer serviceId);

}

package com.jtyu.backend.mapper;

import com.jtyu.backend.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {
    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} ORDER BY create_time DESC")
    List<Message> findByReceiverId(@Param("receiverId") Integer receiverId);

    @Select("SELECT * FROM message WHERE receiver_id = #{receiverId} AND is_read = 0 ORDER BY create_time DESC")
    List<Message> findUnreadByReceiverId(@Param("receiverId") Integer receiverId);

    @Select("SELECT * FROM message WHERE message_id = #{messageId}")
    Message findById(@Param("messageId") Integer messageId);

    @Insert("INSERT INTO message (receiver_id, sender_id, type, content, related_id) " +
            "VALUES (#{receiverId}, #{senderId}, #{type}, #{content}, #{relatedId})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    int insert(Message message);

    @Update("UPDATE message SET is_read = 1 WHERE message_id = #{messageId}")
    int markAsRead(@Param("messageId") Integer messageId);

    @Update("UPDATE message SET is_read = 1 WHERE receiver_id = #{receiverId}")
    int markAllAsRead(@Param("receiverId") Integer receiverId);

    @Delete("DELETE FROM message WHERE message_id = #{messageId}")
    int deleteById(@Param("messageId") Integer messageId);
}

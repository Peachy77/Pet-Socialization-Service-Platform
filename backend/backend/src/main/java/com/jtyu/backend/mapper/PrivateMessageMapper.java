package com.jtyu.backend.mapper;

import com.jtyu.backend.model.PrivateMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PrivateMessageMapper {
    @Select("SELECT * FROM private_message WHERE " +
            "(sender_id = #{userId1} AND receiver_id = #{userId2}) OR " +
            "(sender_id = #{userId2} AND receiver_id = #{userId1}) " +
            "ORDER BY create_time ASC")
    List<PrivateMessage> findConversation(@Param("userId1") Integer userId1, @Param("userId2") Integer userId2);

    @Select("SELECT * FROM private_message WHERE sender_id = #{userId} OR receiver_id = #{userId} ORDER BY create_time DESC")
    List<PrivateMessage> findByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM private_message WHERE receiver_id = #{receiverId} AND is_read = 0 ORDER BY create_time ASC")
    List<PrivateMessage> findUnreadByReceiverId(@Param("receiverId") Integer receiverId);

    @Select("SELECT * FROM private_message WHERE message_id = #{messageId}")
    PrivateMessage findById(@Param("messageId") Integer messageId);

    @Insert("INSERT INTO private_message (sender_id, receiver_id, content, images) " +
            "VALUES (#{senderId}, #{receiverId}, #{content}, #{images})")
    @Options(useGeneratedKeys = true, keyProperty = "messageId")
    int insert(PrivateMessage privateMessage);

    @Update("UPDATE private_message SET is_read = 1 WHERE message_id = #{messageId}")
    int markAsRead(@Param("messageId") Integer messageId);

    @Update("UPDATE private_message SET is_read = 1 WHERE sender_id = #{senderId} AND receiver_id = #{receiverId}")
    int markAllAsRead(@Param("senderId") Integer senderId, @Param("receiverId") Integer receiverId);

    @Delete("DELETE FROM private_message WHERE message_id = #{messageId}")
    int deleteById(@Param("messageId") Integer messageId);

}

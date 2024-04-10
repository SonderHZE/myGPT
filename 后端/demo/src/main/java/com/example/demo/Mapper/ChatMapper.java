package com.example.demo.Mapper;

import com.example.demo.Pojo.Chat;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.type.JdbcType;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ChatMapper {


    @Select("select * from chats where userID = #{userID} order by time desc")
    List<Chat> getAllChatList(String userID);


    @Insert("INSERT INTO chats(userID, chatTitle, time, chatModel) VALUES(#{chat.userID}, #{chat.chatTitle}, #{chat.time}, #{chat.chatModel})")
    @Options(useGeneratedKeys = true, keyProperty = "chat.chatID", keyColumn = "chatID", statementType = StatementType.PREPARED)
    void insertChat(@Param("chat") Chat chat);

    @Update("update chats set time = #{time} where chatID = #{chatID}")
    void updateChatTime(Integer chatID, Timestamp time);
}

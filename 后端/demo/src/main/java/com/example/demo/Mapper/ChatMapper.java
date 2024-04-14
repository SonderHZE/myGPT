package com.example.demo.Mapper;

import com.example.demo.Pojo.Chat;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.StatementType;

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

    @Update("update chats set chatTitle = #{chatTitle} where chatID = #{chatID} and userID = #{userID}")
    Integer renameChat(Integer chatID, String chatTitle, Integer userID);

    @Delete("delete from chats where chatID = #{chatID} and userID = #{i}")
    Integer deleteChat(Integer chatID, int i);
}

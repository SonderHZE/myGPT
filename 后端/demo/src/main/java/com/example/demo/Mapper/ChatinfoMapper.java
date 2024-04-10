package com.example.demo.Mapper;

import com.example.demo.Pojo.Chat;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ChatinfoMapper {
    @Update("update chatinfo set messageList = #{messageList} where chatID = #{chatID}")
    void updateMessageList(Integer chatID, String messageList);


    @Select("select messageList from chatinfo where chatID = #{chatID}")
    String getMessageList(Integer chatID);

    @Insert("INSERT INTO chatinfo(chatID, messageList) VALUES(#{chatID}, #{messageList})")
    void insertMessageList(Integer chatID, String messageList);
}

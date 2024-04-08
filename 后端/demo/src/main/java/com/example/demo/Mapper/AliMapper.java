package com.example.demo.Mapper;

import com.example.demo.Pojo.AliSession;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface AliMapper {
    @Insert("insert into alisession(messageList, title) values(#{messageList}, #{title})")
    public Integer insertAliSession(String messageList, String title);

    @Select("select messageList from alisession where sessionID=#{id}")
    public String selectAliSession(Integer id);

    @Update("update alisession set messageList=#{messageList}, time=#{timestamp} where sessionID=#{sessionID}")
    Integer updateAliSession(Integer sessionID, String messageList, Timestamp timestamp);

    @Select("select sessionID from alisession where messageList=#{messageList}")
    Integer selectAliSessionID(String messageList);

    @Select("select * from alisession where sessionID=#{sessionID}")
    AliSession selectAliSessionBySessionID(Integer sessionIDList);

    @Update("update alisession set title=#{title} where sessionID=#{sessionID}")
    Integer updateAliSessionTitle(Integer sessionID, String title);

    @Delete("delete from alisession where sessionID=#{sessionID}")
    Integer deleteAliSession(Integer sessionID);


    @Select("select * from alisession " +
            "join usertosession u on alisession.sessionID = u.sessionID " +
            "where u.userID=#{userID} " +
            "order by alisession.time desc")
    List<AliSession> selectAliSessionByUserID(Integer userID);
}

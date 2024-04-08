package com.example.demo.Mapper;

import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserToSessionMapper {

    @Insert("insert into usertosession(userID, sessionID, time) values(#{userID}, #{sessionID}, #{timestamp})")
    public void insertUserToSession(Integer userID, Integer sessionID, Timestamp timestamp);

    @Update("update usertosession set time = #{timestamp} where sessionID = #{sessionID}")
    void updateTime(Integer sessionID, Timestamp timestamp);

    @Select("select sessionID from usertosession where userID=#{userID}")
    List<Integer> selectSessionIDByUserID(Integer userID);

    @Select("select userID from usertosession where sessionID=#{sessionID}")
    Integer selectUserIDBySessionID(Integer sessionID);

    @Delete("delete from usertosession where sessionID=#{sessionID}")
    Integer deleteUserToSession(Integer sessionID);
}

package com.example.demo.Mapper;

import com.example.demo.Pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select count(*) from users where userName = #{userName} or mobilePhone = #{mobilePhone}")
    Integer checkUser(String userName, String mobilePhone);

    @Insert("insert into users(userName, password, mobilePhone) values(#{userName}, #{password}, #{mobilePhone})")
    Integer insertUser(User user);

    @Select("select * from users where userName = #{userName}")
    User getUserByUserName(String userName);

    @Select("select * from users where userName = #{userName} or mobilePhone = #{mobilePhone}")
    User getUserByUserNameOrMobilePhone(String userName, String mobilePhone);

    @Select("select * from users where userID = #{userID}")
    User getUserByUserID(int i);
}

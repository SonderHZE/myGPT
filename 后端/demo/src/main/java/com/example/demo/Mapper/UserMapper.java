package com.example.demo.Mapper;

import com.example.demo.Pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

    @Select("select count(*) from users where userName = #{userName} or mobilePhone = #{mobilePhone}")
    Integer checkUser(String userName, String mobilePhone);

    @Insert("insert into users(userName, password, mobilePhone, defaultModel, defaultPrompt) values(#{userName}, #{password}, #{mobilePhone}, '通义千问', 'you are a helpful assistant')")
    Integer insertUser(User user);

    @Select("select * from users where userName = #{userName}")
    User getUserByUserName(String userName);

    @Select("select * from users where userName = #{userName} or mobilePhone = #{mobilePhone}")
    User getUserByUserNameOrMobilePhone(String userName, String mobilePhone);

    @Select("select * from users where userID = #{userID}")
    User getUserByUserID(int i);

    @Update("update users set userName = #{userName}, mobilePhone = #{mobilePhone} where userID = #{userID}")
    Integer updateUserProfile(User user);

    @Update("update users set password = #{s} where userID = #{userID}")
    Integer updatePassword(String s, int userID);

    @Update("update users set defaultModel = #{defaultModel}, defaultPrompt = #{defaultPrompt} where userID = #{userID}")
    Integer updateUserSetting(String defaultModel, String defaultPrompt, int userID);
}

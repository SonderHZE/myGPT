package com.example.demo.Mapper;

import com.example.demo.Pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into user(username, password, email) " +
            "values(#{username}, #{password}, #{email})")
    Integer insertUser(User user);
    @Select("select * from user where username=#{username}")
    User selectUserByUsername(String username);
}

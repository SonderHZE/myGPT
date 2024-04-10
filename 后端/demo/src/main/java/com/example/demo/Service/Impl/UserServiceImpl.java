package com.example.demo.Service.Impl;

import com.example.demo.Mapper.ChatMapper;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Pojo.Chat;
import com.example.demo.Pojo.Result;
import com.example.demo.Pojo.User;
import com.example.demo.Service.UserService;
import com.example.demo.Utils.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    ChatMapper chatMapper;


    @Override
    @Transactional
    public Result userRegister(User user) {
        //1. 检查用户名与手机号的唯一性
        Integer count = userMapper.checkUser(user.getUserName(), user.getMobilePhone());
        if (count > 0) {
            return Result.error("用户名或手机号已存在");
        }

        //2. 密码加密
        user.setPassword(HashUtil.saltHash(user.getPassword()));

        //3. 插入数据库
        count = userMapper.insertUser(user);
        if (count == 0) {
            return Result.error("注册失败");
        }
        user = userMapper.getUserByUserName(user.getUserName());
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result userLogin(String userName, String password) {
        //1. 根据用户名或者手机号查询用户
        User user = userMapper.getUserByUserNameOrMobilePhone(userName, userName);

        //2. 检查用户是否存在
        if (user == null) {
            return Result.error("用户不存在");
        }

        //3. 检查密码是否正确
        if (!HashUtil.saltHash(password).equals(user.getPassword())) {
            return Result.error("密码错误");
        }


        //4. 返回用户信息
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result getAllChatList(String userID) {
        //1. 查询用户的所有聊天记录
        List<Chat> chatList = chatMapper.getAllChatList(userID);

        //2. 返回聊天记录
        return Result.success(chatList);
    }
}

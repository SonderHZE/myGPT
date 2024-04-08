package com.example.demo.Service.Impl;

import com.example.demo.Mapper.AliMapper;
import com.example.demo.Mapper.UserMapper;
import com.example.demo.Mapper.UserToSessionMapper;
import com.example.demo.Pojo.AliSession;
import com.example.demo.Pojo.User;
import com.example.demo.Service.UserService;
import com.example.demo.Utils.HashUtil;
import com.example.demo.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserToSessionMapper userToSessionMapper;
    @Autowired
    AliMapper aliMapper;

    @Override
    public List<AliSession> getSessionList(Integer userID) {
        //1. 根据userID从userToSessionMapper中获取sessionID,再根据sessionID从aliMapper中获取AliSession
        List<AliSession> sessionList = new ArrayList<>();
        sessionList = aliMapper.selectAliSessionByUserID(userID);

        return sessionList;
    }

    @Transactional
    public User userRegister(User user) {
        //1. 用户是否存在
        User existUser = userMapper.selectUserByUsername(user.getUsername());

        //2. 用户已经存在
        if (existUser != null) {
            return null;
        }

        //3. 用户不存在，插入用户
        Integer row = userMapper.insertUser(user);
        if(row == 1) {
            return user;
        }else{
            return null;
        }
    }

    public User userLogin(User user) {
        String password = user.getPassword();
        user = userMapper.selectUserByUsername(user.getUsername());
        //如果为空
        if(user == null) {
            return user;
        }

        // 比对密码是否正确
        String databasePassword = user.getPassword();
        if(!HashUtil.verifyHash(password, databasePassword)) {
            user.setUsername("密码不正确");
            return user;
        }

        // 生成JWT令牌
        StringBuilder token = new StringBuilder(JWTUtil.generateUserJWT(user));
        user.setToken(token.toString());
        return user;
    }

    @Transactional
    public Integer renameChat(Integer sessionID, String title, Integer userID) {
        //1. 通过sessionID获得userID
        Integer selectUserID = userToSessionMapper.selectUserIDBySessionID(sessionID);
        if(!userID.equals(selectUserID)) {
            return 0;
        }

        //2. 修改title
        Integer row = aliMapper.updateAliSessionTitle(sessionID, title);

        return row;
    }

    @Transactional
    public Integer deleteChat(Integer sessionID, Integer userID) {
        //1. 通过sessionID获得userID
        Integer selectUserID = userToSessionMapper.selectUserIDBySessionID(sessionID);
        if(!userID.equals(selectUserID)) {
            return 0;
        }

        //2. 先删除userToSession中的记录
        Integer row = userToSessionMapper.deleteUserToSession(sessionID);
        if(row == 0) {
            return 0;
        }

        //3. 删除aliSession中的记录
        row = aliMapper.deleteAliSession(sessionID);


        return row;
    }
}

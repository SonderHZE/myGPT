package com.example.demo.Service;

import com.example.demo.Pojo.AliSession;
import com.example.demo.Pojo.User;

import java.util.List;

public interface UserService {
    User userRegister(User user);

    User userLogin(User user);

    List<AliSession> getSessionList(Integer userID);

    Integer renameChat(Integer sessionID, String title, Integer userID);

    Integer deleteChat(Integer sessionID, Integer userID);
}

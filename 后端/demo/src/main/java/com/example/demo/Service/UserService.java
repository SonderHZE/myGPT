package com.example.demo.Service;

import com.example.demo.Pojo.Chat;
import com.example.demo.Pojo.Result;
import com.example.demo.Pojo.User;

import java.util.List;

public interface UserService {
    Result userRegister(User user);

    Result userLogin(String userName, String password);

    Result getAllChatList(String userID);
}

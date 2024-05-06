package com.example.demo.Service;

import com.example.demo.Pojo.Result;
import com.example.demo.Pojo.User;

public interface UserService {
    Result userRegister(User user);

    Result userLogin(String userName, String password);

    Result getAllChatList(String userID);

    Result renameChat(Integer chatID, String chatTitle, Integer userID);

    Result deleteChat(Integer chatID, int i);

    Result getUserInfo(int i);
}

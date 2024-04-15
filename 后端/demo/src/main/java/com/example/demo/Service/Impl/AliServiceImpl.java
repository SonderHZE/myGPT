package com.example.demo.Service.Impl;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.example.demo.Mapper.ChatMapper;
import com.example.demo.Mapper.ChatinfoMapper;
import com.example.demo.Pojo.Chat;
import com.example.demo.Pojo.Result;
import com.example.demo.Utils.ChatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;

@Service
public class AliServiceImpl {
    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private ChatinfoMapper chatinfoMapper;

    static {
        Constants.apiKey="sk-d4747b14216e4ecc93ffeb5f9a2a4dc1";
    }

    @Transactional
    public Chat aliChat(Chat chat, SseEmitter sseEmitter, Integer userID) throws NoApiKeyException, InputRequiredException, IOException {
        Timestamp time = new Timestamp(System.currentTimeMillis());

        //1. 获取对话ID
        Integer chatID = chat.getChatID();
        if(chatID.equals(-1)){
            // 是一个新对话，需要插入数据库
            chat.setTime(time);
            chat.setChatTitle(chat.getInputValue());
            chatMapper.insertChat(chat);
        }else{
            // 是一个已有对话
            chat.setTime(time);
            if(chat.getMessageList()==null){
                System.out.println("redis没命中，重新从数据库中获取");
                chat.setMessageList(chatinfoMapper.getMessageList(chatID));
            }
            // 更新对话时间
            chatMapper.updateChatTime(chatID, time);
        }

        //2. 调用通义千问接口
        ChatUtils.aliStreamCall(chat, sseEmitter);

        return chat;
    }

    public Result getChatInfo(Integer chatID) {
        String messageList = chatinfoMapper.getMessageList(chatID);
        return Result.success(messageList);
    }
}

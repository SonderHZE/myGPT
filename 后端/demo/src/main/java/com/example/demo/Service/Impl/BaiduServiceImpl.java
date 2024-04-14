package com.example.demo.Service.Impl;

import com.example.demo.Mapper.ChatMapper;
import com.example.demo.Mapper.ChatinfoMapper;
import com.example.demo.Pojo.Chat;
import com.example.demo.Utils.ChatUtils;
import okhttp3.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;

@Service
public class BaiduServiceImpl {
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private ChatinfoMapper chatinfoMapper;

    public static final String API_KEY = "IrwsY0SE5ba9KvhmMXcWzPTS";
    public static final String SECRET_KEY = "IhqYv5CqzyG1PvKV0YZCvBAO9vDZNH1O";
    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();//创建OkHttpClient对象


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    public static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }

    public Chat baiduChat(Chat chat, SseEmitter sseEmitter, Integer userID) throws IOException {
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
            // messageList为空，说明redis没命中，需要重新从数据库中获取
            if(chat.getMessageList()==null){
                System.out.println("redis没命中，重新从数据库中获取");
                chat.setMessageList(chatinfoMapper.getMessageList(chatID));
            }
            // 更新对话时间
            chatMapper.updateChatTime(chatID, time);
        }

        //2. 调用百度接口
        ChatUtils.baiduStreamCall(chat, sseEmitter);

        return chat;
    }
}

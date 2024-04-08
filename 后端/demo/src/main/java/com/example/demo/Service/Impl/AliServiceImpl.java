package com.example.demo.Service.Impl;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import com.example.demo.Mapper.AliMapper;
import com.example.demo.Mapper.UserToSessionMapper;
import com.example.demo.Pojo.AliSession;
import com.example.demo.Utils.AliUtils;
import io.reactivex.Flowable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class AliServiceImpl {
    @Autowired
    private AliMapper aliMapper;
    @Autowired
    UserToSessionMapper userToSessionMapper;

    static {
        Constants.apiKey="sk-d4747b14216e4ecc93ffeb5f9a2a4dc1";
    }

    public void streamCall(AliSession aliSession, SseEmitter sseEmitter) throws NoApiKeyException, InputRequiredException, IOException {
        //1. 获取用户输入的问题，并创建一个List<Message>对象
        String question = aliSession.getQuestion();
        String messageList = aliSession.getMessageList();

        List<Message> messages = new ArrayList<>();

        //2. 如果aliSession中的messageList不为空，则用其构建一个Message对象
        if (messageList != null) {
            messages = AliUtils.createMessageList(messageList);
        }

        //2. 创建新会话
        Generation generation = new Generation();

        Message userMessage =
                Message.builder().
                        role(Role.USER.getValue()).
                        content(question).
                        build();
        messages.add(userMessage);

        GenerationParam generationParam = GenerationParam.builder()
                .model("qwen-turbo")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8).enableSearch(true)
                .incrementalOutput(true)
                .build();

        Flowable<GenerationResult> result = generation.streamCall(generationParam);
        StringBuilder fullContent = new StringBuilder();
        result.blockingForEach(message -> {
            StringBuilder content = new StringBuilder(message.getOutput().getChoices().get(0).getMessage().getContent());
            System.out.println(content);
            fullContent.append(content);
            sseEmitter.send(content.toString());
        });

        System.out.println(fullContent);
        StringBuilder userContent = new StringBuilder(aliSession.getQuestion());
        StringBuilder assistantContent = new StringBuilder(fullContent);
        aliSession.setMessageList(aliSession.getMessageList() + "user: " + userContent + "\nassistant: " + assistantContent + "\n");

        sseEmitter.send("CHAT COMPLETED!");
        sseEmitter.send(aliSession.getSessionID());
        sseEmitter.complete();
    }


    @Transactional
    public Integer aliChat(AliSession aliSession, SseEmitter sseEmitter, Integer userID) {
        try {
            // 如果是新对话，则插入一条新的对话记录，否则从数据库中提取一条
            if (aliSession.getSessionID().equals(-1)) {
                aliSession.setTitle(aliSession.getQuestion());
                aliMapper.insertAliSession("", aliSession.getQuestion());
                Integer sessionID = aliMapper.selectAliSessionID("");
                aliSession.setSessionID(sessionID);
                aliSession.setMessageList("");
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                userToSessionMapper.insertUserToSession(userID, sessionID, timestamp);
            } else {
                String temp = aliMapper.selectAliSession(aliSession.getSessionID());
                aliSession.setMessageList(temp);
            }

            streamCall(aliSession, sseEmitter);

            // 将对话记录更新到数据库中
            Integer row = aliMapper.updateAliSession(aliSession.getSessionID(), aliSession.getMessageList(),new Timestamp(System.currentTimeMillis()));
            if(row == 0) {
                throw new Exception("更新对话记录失败");
            }
            userToSessionMapper.updateTime(aliSession.getSessionID(), new Timestamp(System.currentTimeMillis()));

            return aliSession.getSessionID();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

}

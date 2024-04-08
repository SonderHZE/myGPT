package com.example.demo.Utils;

import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;

import java.util.ArrayList;
import java.util.List;

public class AliUtils {
    //aliSession.setMessageList("User: " + userContent + "\nAssistant: " + assistantContent + "\n");
    public static List<Message> createMessageList(String messageList) {
        //1. 将messageList转换为List<Message>对象
        List<Message> messages = new ArrayList<>();

        //2. 以"User: "和"Assistant: "为分隔符，将messageList分割为多个Message对象
        for(String message : messageList.split("User: |Assistant: ")) {
            if(message.startsWith("user: ")) {
                messages.add(Message.builder().role(Role.USER.getValue()).content(message.substring(6)).build());
            } else if(message.startsWith("assistant: ")) {
                messages.add(Message.builder().role(Role.ASSISTANT.getValue()).content(message.substring(11)).build());
            }
        }

        return messages;
    }
}

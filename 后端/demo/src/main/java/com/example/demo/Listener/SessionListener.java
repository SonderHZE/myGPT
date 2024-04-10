package com.example.demo.Listener;

import com.example.demo.Mapper.ChatinfoMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Enumeration;

@Component
public class SessionListener implements HttpSessionListener {
    @Autowired
    private ChatinfoMapper chatinfoMapper;

    // Session销毁时的操作
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("Session销毁");
        HttpSession session = se.getSession();
        // 获取session所有以"MessageList"开头的属性名
        Enumeration<String> attributeNames = session.getAttributeNames();

        // 遍历所有属性名，打印以"MessageList"开头的属性名
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            if (attributeName.startsWith("messageList")) {
                // 写入数据库
                Integer chatID = Integer.parseInt(attributeName.substring(11));
                String messageList = (String) session.getAttribute(attributeName);

                // 如果不存在对应的chatID，则插入数据库
                if (chatinfoMapper.getMessageList(chatID) == null) {
                    chatinfoMapper.insertMessageList(chatID, messageList);
                }else{
                    // 如果存在对应的chatID，则更新数据库
                    chatinfoMapper.updateMessageList(chatID, messageList);
                }
            }
        }
    }
}

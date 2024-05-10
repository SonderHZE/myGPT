package com.example.demo.Service;

import com.example.demo.Pojo.Chat;
import com.example.demo.Pojo.Result;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AliService {
    Chat aliChat(Chat chat, SseEmitter sseEmitter, Integer userID) throws Exception;
    Result imageCreation(String prompt, String size, Integer n) throws Exception;
}

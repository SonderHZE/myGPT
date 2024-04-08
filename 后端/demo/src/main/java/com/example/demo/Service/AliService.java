package com.example.demo.Service;

import com.example.demo.Pojo.AliSession;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AliService {
    void aliChat(AliSession aliSession, SseEmitter sseEmitter);
}

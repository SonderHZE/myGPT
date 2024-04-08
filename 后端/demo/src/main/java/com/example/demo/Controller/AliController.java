package com.example.demo.Controller;

import com.example.demo.Pojo.AliSession;
import com.example.demo.Service.Impl.AliServiceImpl;
import com.example.demo.Utils.JWTUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class AliController {
    @Autowired
    private AliServiceImpl aliServiceImpl;

    @GetMapping("/aliChat")
    public SseEmitter handleGetRequest(@RequestParam String question, @RequestParam Integer sessionID,
                                       @RequestParam Integer userID,
                                       HttpServletRequest request, HttpServletResponse response) {
        SseEmitter sseEmitter = new SseEmitter();

        AliSession aliInputSession = new AliSession();
        aliInputSession.setQuestion(question);
        aliInputSession.setSessionID(sessionID);

        request.getSession().setAttribute("aliSession", aliInputSession);

        response.setHeader("Set-Cookie", "JSESSIONID=" + request.getSession().getId() + "; SameSite=None; Secure");

        new Thread(() -> {
            try {
                aliServiceImpl.aliChat(aliInputSession, sseEmitter, userID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        return sseEmitter;
    }
}

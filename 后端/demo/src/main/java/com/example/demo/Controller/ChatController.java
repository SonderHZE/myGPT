package com.example.demo.Controller;

import com.example.demo.Pojo.Chat;
import com.example.demo.Pojo.Result;
import com.example.demo.Service.Impl.AliServiceImpl;
import com.example.demo.Utils.CookieUtil;
import com.example.demo.Utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class ChatController {
    @Autowired
    private AliServiceImpl aliServiceImpl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 调用通义千问接口
    @GetMapping("/aliChat")
    public SseEmitter handleGetRequest(@RequestParam String chatModel,
                                       @RequestParam Integer chatID,
                                       @RequestParam String inputValue,
                                       HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        SseEmitter sseEmitter = new SseEmitter();

        //1. 从cookie中获取token
        String token = CookieUtil.getCookieValue(request, "token");
        if (token == null) {
            sseEmitter.send("请先登录");
            return sseEmitter;
        }
        Integer userID = Integer.parseInt(JWTUtil.parseToken(token, "userID"));



        //2. 实例化Chat对象
        // 使用AtomicReference包装Chat对象，以便在多线程环境下修改Chat对象
        AtomicReference<Chat> chat = new AtomicReference<>(new Chat());
        chat.get().setUserID(userID);
        chat.get().setChatID(chatID);
        chat.get().setInputValue(inputValue);
        chat.get().setChatModel(chatModel);

//        HttpSession session = request.getSession();
//        session.setMaxInactiveInterval(60*30);
//        String JSID = CookieUtil.getCookieValue(request, "JSESSIONID");
//        if(JSID==null||!JSID.equals(session.getId())){
//            ResponseCookie cookie = ResponseCookie.from("JSESSIONID", session.getId())
//                    .httpOnly(true)
//                    .sameSite("None")
//                    .secure(true)
//                    .path("/")
//                    .build();
//
//
//            response.addHeader("Set-Cookie", cookie.toString());
//        }
//
//        //3. 从session中获取对话记录
//        if(!chatID.equals(-1)){
//            String key = "messageList"+chatID.toString();
//            String value = (String) session.getAttribute(key);
//            if(value!=null){
//                chat.get().setMessageList(value);
//            }
//        }

        //3. 从redis中获取对话记录
        if(stringRedisTemplate.hasKey("messageList"+chatID)){
            String value = stringRedisTemplate.opsForValue().get("messageList"+chatID);
            chat.get().setMessageList(value);

            // 更新备份键的过期时间
            stringRedisTemplate.expire("backupKey"+"messageList"+chatID, 60*30+20, TimeUnit.SECONDS);
        }

        Thread thread = new Thread(() -> {
            try {
                chat.set(aliServiceImpl.aliChat(chat.get(), sseEmitter, userID));
            } catch (Exception e) {
                e.printStackTrace();
            }

          try{
              // 将对话记录存入redis以及过期时间
              stringRedisTemplate.opsForValue().set("messageList"+chat.get().getChatID(), chat.get().getMessageList());
              stringRedisTemplate.expire("messageList"+chat.get().getChatID(), 60*30, TimeUnit.SECONDS);
              stringRedisTemplate.opsForValue().set("backupKey"+"messageList"+chat.get().getChatID(), chat.get().getMessageList());
              stringRedisTemplate.expire("backupKey"+"messageList"+chat.get().getChatID(), 60*30+20, TimeUnit.SECONDS);
          }catch (Exception e){
              e.printStackTrace();
            }
        });
        thread.start();


        return sseEmitter;
    }

    @GetMapping("/getChatInfo")
    public Result getChatInfo(@RequestParam Integer chatID, HttpServletRequest request){
        Result result = new Result();
//        // 先从session中获取对话记录，如果有则直接返回，否则去数据库查询
//        HttpSession session = request.getSession();
//        String messageList = (String) session.getAttribute("messageList"+chatID);
//        if(messageList!=null){
//            result = Result.success(messageList);
//            return result;
//        }

        // 从redis中获取对话记录
        if(stringRedisTemplate.hasKey("messageList"+chatID)){
            String value = stringRedisTemplate.opsForValue().get("messageList"+chatID);
            result = Result.success(value);

            // 更新备份键的过期时间
            stringRedisTemplate.expire("backupKey"+"messageList"+chatID, 60*30+20, TimeUnit.SECONDS);
            return result;
        }

        // 缓存不命中，去数据库查询，然后存入redis
        result = aliServiceImpl.getChatInfo(chatID);
        if(result.getStatus().equals("success")) {
            String messageList = (String) result.getData();


            // 将对话记录存入redis以及过期时间
            stringRedisTemplate.opsForValue().set("messageList"+chatID, messageList);
            stringRedisTemplate.expire("messageList"+chatID, 60*30, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set("backupKey"+"messageList"+chatID, messageList);
            stringRedisTemplate.expire("backupKey"+"messageList"+chatID, 60*30+20, TimeUnit.SECONDS);
        }

        return result;
    }

}

package com.example.demo.Controller;

import com.example.demo.Pojo.Chat;
import com.example.demo.Pojo.Result;
import com.example.demo.Service.Impl.AliServiceImpl;
import com.example.demo.Service.Impl.BaiduServiceImpl;
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
    private BaiduServiceImpl baiduServiceImpl;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 调用Ai接口
    @GetMapping("/aiChat")
    public SseEmitter handleGetRequest(@RequestParam String chatModel, @RequestParam Integer chatID, @RequestParam String inputValue,
                                       @RequestParam Float temperature, @RequestParam Double top_p, @RequestParam String system,
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
        chat.get().setTemperature(temperature);
        chat.get().setTop_p(top_p);
        chat.get().setSystem(system);


        //3. 从redis中获取对话记录
        if(stringRedisTemplate.hasKey("messageList"+chatID)){
            String value = stringRedisTemplate.opsForValue().get("messageList"+chatID);
            chat.get().setMessageList(value);

            String systemValue = stringRedisTemplate.opsForValue().get("chatinfo:chatID"+chatID+":system");

            // 更新备份键的过期时间
            stringRedisTemplate.expire("backupKey"+"messageList"+chatID, 60*30+20, TimeUnit.SECONDS);
        }


        Thread thread = new Thread(() -> {
            try {
                if(chatModel.equals("通义千问")){
                    chat.set(aliServiceImpl.aliChat(chat.get(), sseEmitter, userID));
                }else{
                    chat.set(baiduServiceImpl.baiduChat(chat.get(), sseEmitter, userID));
                }
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

            if(messageList == null){
                return Result.error("没有对话记录");
            }
            // 将对话记录存入redis以及过期时间
            stringRedisTemplate.opsForValue().set("messageList"+chatID, messageList);
            stringRedisTemplate.expire("messageList"+chatID, 60*30, TimeUnit.SECONDS);
            stringRedisTemplate.opsForValue().set("backupKey"+"messageList"+chatID, messageList);
            stringRedisTemplate.expire("backupKey"+"messageList"+chatID, 60*30+20, TimeUnit.SECONDS);
        }

        return result;
    }

}

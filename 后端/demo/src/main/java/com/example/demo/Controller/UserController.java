package com.example.demo.Controller;

import com.example.demo.Pojo.Chat;
import com.example.demo.Pojo.Result;
import com.example.demo.Pojo.User;
import com.example.demo.Service.Impl.UserServiceImpl;
import com.example.demo.Utils.CookieUtil;
import com.example.demo.Utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    //用户注册
    @PostMapping("/user/register")
    public Result userRegister(@RequestBody User user) {
        //1. 检验数据是否为空
        if (user == null || user.getUserName() == null || user.getPassword() == null || user.getMobilePhone() == null) {
            return Result.error("数据不能为空");
        }

        //2. 传递给service层
        Result result = userService.userRegister(user);
        return result;
    }

    //用户登录
    @PostMapping("/user/login")
    public Result userLogin(@RequestBody User user,
                            HttpServletResponse response, HttpServletRequest request) {
        Result result = userService.userLogin(user.getUserName(), user.getPassword());

        // 如果登录失败，返回错误信息
        if (result.getStatus() == "error") return result;

        // 根据信息生成token,并且允许cookie跨域
        user = (User) result.getData();
        JWTUtil.generateToken(user);
        ResponseCookie cookie = ResponseCookie.from("token", JWTUtil.generateToken(user))
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .path("/")
                .build();

        if(request.getSession(false) == null){
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(60*30);

            ResponseCookie JSESSIONID = ResponseCookie.from("JSESSIONID", session.getId())
                    .httpOnly(true)
                    .sameSite("None")
                    .secure(true)
                    .path("/")
                    .build();
            response.addHeader("Set-Cookie", JSESSIONID.toString());
        }

        response.addHeader("Set-Cookie", cookie.toString());



        return result;
    }

    //获取所有聊天记录
    @GetMapping("/user/getAllChatList")
    public Result getAllChatList(HttpServletRequest request) {
        String token = CookieUtil.getCookieValue(request, "token");
        String userID = JWTUtil.parseToken(token, "userID");

        return userService.getAllChatList(userID);
    }

    //用户登出
    @GetMapping("/user/logout")
    public Result userLogout(HttpServletRequest request, HttpServletResponse response){
        CookieUtil.deleteCookie(request, response, "token");
        return Result.success("登出成功");
    }

    //重命名聊天记录
    @PostMapping("/user/renameChat")
    public Result renameChat(@RequestBody Chat chat, HttpServletRequest request){
        String token = CookieUtil.getCookieValue(request, "token");
        if(token == null) return Result.error("请先登录");
        String UserID = JWTUtil.parseToken(token, "userID");

        Integer chatID = chat.getChatID();
        String chatTitle = chat.getChatTitle();

        return userService.renameChat(chatID, chatTitle, Integer.parseInt(UserID));

    }

    //删除聊天记录
    @PostMapping("/user/deleteChat")
    public Result deleteChat(@RequestBody Chat chat, HttpServletRequest request){
        String token = CookieUtil.getCookieValue(request, "token");
        if(token == null) return Result.error("请先登录");
        String UserID = JWTUtil.parseToken(token, "userID");

        Integer chatID = chat.getChatID();

        return userService.deleteChat(chatID, Integer.parseInt(UserID));

    }
}

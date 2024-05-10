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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

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

        // 检查redis中是否有该用户的token
        String redisToken = stringRedisTemplate.opsForValue().get("user::" + user.getUserID()+"::token");
        if(redisToken != null){
            return Result.error("用户已登录");
        }

        String token = JWTUtil.generateToken(user);
        ResponseCookie cookie = ResponseCookie.from("token", token)
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

        // 将JWT令牌放入redis
        stringRedisTemplate.opsForValue().set("user::" + user.getUserID()+"::token", token, 60*30, java.util.concurrent.TimeUnit.SECONDS);
        // 设置登录时间
        stringRedisTemplate.opsForValue().set("user::" + user.getUserID()+"::loginTime", String.valueOf(System.currentTimeMillis()), 60*30, java.util.concurrent.TimeUnit.SECONDS);


        return result;
    }

    @GetMapping("/user/login")
    public Result userLogin(HttpServletRequest request){
        String token = CookieUtil.getCookieValue(request, "token");
        if(token == null) return Result.error("请先登录");
        String userID = JWTUtil.parseToken(token, "userID");

        // 对比redis中的token
        String redisToken = stringRedisTemplate.opsForValue().get("user::" + userID + "::token");
        if(redisToken == null || !redisToken.equals(token)){
            return Result.error("请先登录");
        }

        // 续期
        stringRedisTemplate.expire("user::" + userID + "::token", 60*30, java.util.concurrent.TimeUnit.SECONDS);
        return userService.getUserInfo(Integer.parseInt(userID));
    }

    //获取用户信息,
    @GetMapping("/user/getUserProfile")
    public Result getUserProFile(HttpServletRequest request, @RequestParam Integer userID){
        String token = CookieUtil.getCookieValue(request, "token");
        if(token == null) return Result.error("请先登录");

        // 判断redis中的token和cookie中的token是否一致
        String redisToken = stringRedisTemplate.opsForValue().get("user::" + userID + "::token");
        if(redisToken == null || !redisToken.equals(token)){
            return Result.error("请先登录");
        }

        if(userID.toString().equals(JWTUtil.parseToken(token, "userID"))){
            return userService.getUserInfo(userID);
        }else{
            return Result.error("无权查看");
        }
    }

    @PostMapping("/user/updateUserProfile")
    public Result updateUserProfile(@RequestBody User user, HttpServletRequest request){
        String token = CookieUtil.getCookieValue(request, "token");
        if(token == null) return Result.error("请先登录");
        String userID = JWTUtil.parseToken(token, "userID");

        // 判断redis中的token和cookie中的token是否一致
        String redisToken = stringRedisTemplate.opsForValue().get("user::" + userID + "::token");
        if(redisToken == null || !redisToken.equals(token)){
            return Result.error("请先登录");
        }


        if(user.getUserID().toString().equals(JWTUtil.parseToken(token, "userID"))){
            return userService.updateUserProfile(user);
        }else{
            return Result.error("无权修改");
        }
    }

    // 修改密码
    @PutMapping("/user/updatePassword")
    public Result updatePassword(HttpServletRequest request, @RequestParam String newPassword, @RequestParam String oldPassword, @RequestParam Integer userID){
        String token = CookieUtil.getCookieValue(request, "token");
        if(token == null) return Result.error("请先登录");
        String tokenUserID = JWTUtil.parseToken(token, "userID");

        // 判断redis中的token和cookie中的token是否一致
        String redisToken = stringRedisTemplate.opsForValue().get("user::" + userID + "::token");
        if(redisToken == null || !redisToken.equals(token)){
            return Result.error("请先登录");
        }

        if(userID.toString().equals(tokenUserID)){
            return userService.updatePassword(newPassword, oldPassword, userID);
        }else{
            return Result.error("无权修改");
        }

    }

    // 修改默认模式
    @PutMapping("/user/updateUserSettings")
    public Result updateUserSetting(HttpServletRequest request,  @RequestBody User user){
        String token = CookieUtil.getCookieValue(request, "token");
        if(token == null) return Result.error("请先登录");
        String userID = JWTUtil.parseToken(token, "userID");

        // 判断redis中的token和cookie中的token是否一致
        String redisToken = stringRedisTemplate.opsForValue().get("user::" + userID + "::token");
        if(redisToken == null || !redisToken.equals(token)){
            return Result.error("请先登录");
        }

        // 判断是否是自己的设置
        if(!user.getUserID().toString().equals(JWTUtil.parseToken(token, "userID"))){
            return Result.error("无权修改");
        }

        return userService.updateUserSetting(user.getDefaultModel(), user.getDefaultPrompt(), user.getUserID());
    }


    //获取所有聊天记录
    @GetMapping("/user/getAllChatList")
    public Result getAllChatList(HttpServletRequest request) {
        String token = CookieUtil.getCookieValue(request, "token");
        String userID = JWTUtil.parseToken(token, "userID");

        String redisToken = stringRedisTemplate.opsForValue().get("user::" + userID + "::token");
        if(redisToken == null || !redisToken.equals(token)){
            return Result.error("请先登录");
        }

        return userService.getAllChatList(userID);
    }

    //用户登出
    @PostMapping("/user/logout")
    public Result userLogout(HttpServletRequest request, HttpServletResponse response){
        CookieUtil.deleteCookie(request, response, "token");
        String token = CookieUtil.getCookieValue(request, "token");
        String userID = JWTUtil.parseToken(token, "userID");
        // 删除redis中的token
        stringRedisTemplate.delete("user::" + userID + "::token");
        stringRedisTemplate.delete("user::" + userID + "::loginTime");

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

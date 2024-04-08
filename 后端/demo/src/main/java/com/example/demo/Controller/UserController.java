package com.example.demo.Controller;

import com.example.demo.Pojo.AliSession;
import com.example.demo.Pojo.Result;
import com.example.demo.Pojo.User;
import com.example.demo.Service.Impl.UserServiceImpl;
import com.example.demo.Utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @PostMapping("/user/register")
    public Result register(@RequestBody User user) {
        user = userService.userRegister(user);

        if(user == null) {
            return Result.error("用户已经存在");
        }
        return Result.success(user);
    }

    @PostMapping("/user/login")
    public Result login(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {
        user = userService.userLogin(user);

        if(user== null) {
            return Result.error("用户不存在");
        }
        if("密码不正确".equals(user.getUsername())) {
            return Result.error("密码不正确");
        }

        // 将JWT令牌放入cookie
        String token = user.getToken();
        response.setHeader("Set-Cookie", "token=" + token + "; SameSite=None; Secure;path=/");

        // 将用户信息放入session
        request.getSession().setAttribute("userID", user.getUserID());
        return Result.success(user);
    }

    @GetMapping("/user/sessionList")
    public Result sessionList(@RequestParam Integer userID) {
        if(userID == null) {
            return Result.error("用户未登录");
        }
        List<AliSession> sessionList = userService.getSessionList(userID);

        return Result.success(sessionList);
    }

    @PostMapping("/user/renameChat")
    public Result renameChat(@RequestBody AliSession aliSession){
        Integer row = userService.renameChat(aliSession.getSessionID(), aliSession.getTitle(), aliSession.getUserID());
        if(row == 1) {
            return Result.success("修改成功");
        }else{
            return Result.error("修改失败");
        }
    }

    @PostMapping("/user/deleteChat")
    public Result deleteChat(@RequestBody AliSession aliSession){
        Integer row = userService.deleteChat(aliSession.getSessionID(), aliSession.getUserID());
        if(row == 1) {
            return Result.success("删除成功");
        }else{
            return Result.error("删除失败");
        }
    }
}

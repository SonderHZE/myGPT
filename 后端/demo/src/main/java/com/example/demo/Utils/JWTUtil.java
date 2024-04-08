package com.example.demo.Utils;

import com.example.demo.Pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

public class JWTUtil {
    private final static SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private final static String secret = "mqdqxygyqyklklsys24678hahazyzdysbqdddsy";

    //生成用户JWT令牌
    public static String generateUserJWT(User user){
        //1 生成JWT令牌
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userID", user.getUserID().toString())
                .claim("username", user.getUsername().toString())
                .claim("email", user.getEmail().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SIGNATURE_ALGORITHM, secret)
                .compact();

    }

    //解析JWT令牌
    public static Claims parseJWT(String jwt){
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return claims;
    }

    public static Boolean tokenVerify(HttpServletRequest request, String info, String key) {
        // 1.提取cookie中的token
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 2.验证token
        if (token == null) {
            return false;
        }

        Claims claims = JWTUtil.parseJWT(token);
        String tokenInfo = claims.get(key, String.class);
        if(tokenInfo == null || !tokenInfo.equals(info)) {
            return false;
        }
        return true;
    }

    public static Boolean tokenVefiry(String token, String info, String key) {
        Claims claims = JWTUtil.parseJWT(token);
        String tokenInfo = claims.get(key, String.class);
        if(tokenInfo == null || !tokenInfo.equals(info)) {
            return false;
        }
        return true;
    }
}

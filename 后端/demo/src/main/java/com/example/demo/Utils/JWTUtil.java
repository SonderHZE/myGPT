package com.example.demo.Utils;

import com.example.demo.Pojo.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JWTUtil {
    private final static SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private final static String secret = "mqdqxygyqyklklsys24678hahazyzdysbqdddsy";

    //生成用户JWT令牌
    public static String generateToken(User user) {
        return Jwts.builder()
                .claim("userName", user.getUserName().toString())
                .claim("userID", user.getUserID().toString())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SIGNATURE_ALGORITHM, secret)
                .compact();
    }

    //解析JWT令牌,输入令牌和期望得到的key,
    public static String parseToken(String token, String key) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
        return claims.get(key).toString();
    }
}

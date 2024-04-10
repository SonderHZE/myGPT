package com.example.demo;

import com.example.demo.Utils.CookieUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class DemoApplicationTests {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    void redisTest() {
        stringRedisTemplate.opsForValue().set("test", "test");
    }
}

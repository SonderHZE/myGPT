package com.example.demo.Listener;

import com.example.demo.Mapper.ChatinfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExxpirationListener extends KeyExpirationEventMessageListener {
    @Autowired
    private ChatinfoMapper chatinfoMapper;
    @Autowired
    public RedisKeyExxpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    //当监听到过期key时，会调用这个方法，message是过期的key
    public void onMessage(Message message, byte[] pattern) {
        String expireKey = message.toString();
        if(expireKey.startsWith("backupKey")){
            return;
        }

        Integer chatID = Integer.parseInt(expireKey.substring(11));

        // 从备份键获得数据
        String value = stringRedisTemplate.opsForValue().get("backupKey"+expireKey);

        // 如果数据库中没有chatID对应的数据，将备份键的数据插入数据库
        if(chatinfoMapper.getMessageList(chatID)==null){
            chatinfoMapper.insertMessageList(chatID, value);
        }else{
            // 如果数据库中有chatID对应的数据，将备份键的数据追加到数据库中
            chatinfoMapper.updateMessageList(chatID, value);
        }
    }
}

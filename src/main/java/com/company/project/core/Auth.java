package com.company.project.core;


import org.junit.runner.RunWith;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@Component
public class Auth {
    private StringRedisTemplate stringRedisTemplate;

    public Auth(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String setSession(String uuid) throws Exception {
        String keyid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(keyid,uuid);
        stringRedisTemplate.expire(keyid,30*60, TimeUnit.SECONDS);
        return keyid;
    }
    public String getSession(String key) throws Exception{
        return stringRedisTemplate.opsForValue().get(key);
    }
    public Boolean hasSession(String key){
        if(stringRedisTemplate.hasKey(key)){
            return true;
        }
        return false;
    }
}

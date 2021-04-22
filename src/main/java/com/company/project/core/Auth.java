package com.company.project.core;


import org.junit.runner.RunWith;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

@RunWith(SpringRunner.class)
public class Auth {
    private StringRedisTemplate stringRedisTemplate;

    public Auth(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String setSession(String uuid) throws Exception {
        String keyid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(keyid,uuid);
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
package cc.xiaoxu.cloud.my.wechat.config;

import me.chanjar.weixin.common.redis.WxRedisOps;
import me.chanjar.weixin.common.util.locks.RedisTemplateSimpleDistributedLock;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class LettuceWxRedisOps implements WxRedisOps {

    public StringRedisTemplate stringRedisTemplate;

    public LettuceWxRedisOps(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String getValue(String key) {
        Object o = stringRedisTemplate.opsForValue().get(key);
        return o == null ? null : o.toString();
    }

    @Override
    public void setValue(String key, String value, int expire, TimeUnit timeUnit) {
        if (expire <= 0) {
            stringRedisTemplate.opsForValue().set(key, value);
        } else {
            stringRedisTemplate.opsForValue().set(key, value, expire, timeUnit);
        }
    }

    @Override
    public Long getExpire(String key) {
        long expire = stringRedisTemplate.getExpire(key);
        if (expire > 0) {
            expire = expire / 1000;
        }
        return expire;
    }

    @Override
    public void expire(String key, int expire, TimeUnit timeUnit) {
        stringRedisTemplate.expire(key, expire, timeUnit);
    }

    @Override
    public Lock getLock(String key) {
        return new RedisTemplateSimpleDistributedLock(stringRedisTemplate, key, 60 * 1000);
    }
}

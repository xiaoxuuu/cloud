package cc.xiaoxu.cloud.ai.listener;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis 过期 key 监听
 */
@Configuration
public class RedisListenerConfig {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisListenerConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    public RedisMessageListenerContainer container() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        return container;
    }

    @Bean
    public RedisKeyExpirationListener keyExpirationListener() {
        return new RedisKeyExpirationListener(this.container());
    }
}
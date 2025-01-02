package cc.xiaoxu.cloud.ai.listener;

import cc.xiaoxu.cloud.ai.constants.RedisListenerConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * redis key过期监听
 */
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {

        try {
            process(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void process(Message message) throws InterruptedException {
        String expireKey = message.toString();

        if (!expireKey.startsWith(RedisListenerConstants.PREFIX)) {
            return;
        }
    }
}
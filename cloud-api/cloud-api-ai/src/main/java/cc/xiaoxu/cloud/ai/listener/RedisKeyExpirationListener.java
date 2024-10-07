package cc.xiaoxu.cloud.ai.listener;

import cc.xiaoxu.cloud.ai.constants.RedisListenerConstants;
import cc.xiaoxu.cloud.ai.task.ALiFileStatusCheckTask;
import jakarta.annotation.Resource;
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

    @Resource
    private ALiFileStatusCheckTask aLiFileStatusCheckTask;

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

        // 数据检查
        switch (expireKey) {
            case RedisListenerConstants.FILE_UPLOAD_RESULT_CHECK -> aLiFileStatusCheckTask.aLiFileUploadResultCheck();
            case RedisListenerConstants.FILE_INDEX_RESULT_CHECK -> aLiFileStatusCheckTask.aLiFileIndexResultCheck();
        }

        // 文件上传结果
        if (expireKey.startsWith(RedisListenerConstants.FILE_UPLOAD_RESULT_HANDLE)) {
            aLiFileStatusCheckTask.aLiFileUploadResultCheck(expireKey.replace(RedisListenerConstants.FILE_UPLOAD_RESULT_HANDLE, ""));
        }

        // 文件切片结果
        if (expireKey.startsWith(RedisListenerConstants.FILE_INDEX_RESULT_HANDLE)) {
            aLiFileStatusCheckTask.aLiFileIndexResultCheck(expireKey.replace(RedisListenerConstants.FILE_INDEX_RESULT_HANDLE, ""));
        }
    }
}
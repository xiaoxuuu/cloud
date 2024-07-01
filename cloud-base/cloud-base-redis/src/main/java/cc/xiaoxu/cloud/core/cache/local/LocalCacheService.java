package cc.xiaoxu.cloud.core.cache.local;

import cc.xiaoxu.cloud.core.cache.CacheService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "cache", name = "type", havingValue = "local")
public class LocalCacheService implements CacheService, InitializingBean {

    @Resource(name = "scheduledExecutorService")
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void afterPropertiesSet() {
        log.error("使用本地内存服务器，该功能不适用于微服务");
        // 定时清理过期缓存，注册一个定时线程任务，服务启动 1 秒之后，每隔 1 秒执行一次
        scheduledExecutorService.scheduleAtFixedRate(LocalCache::clearCache, 1, 1, TimeUnit.MINUTES);
        log.error("注册定时清理过期缓存任务");
    }

    @Override
    public <T> void setCacheObject(String key, T value) {
        LocalCache.put(key, value);
    }

    @Override
    public <T> void setCacheObject(String key, T value, Long timeout, TimeUnit timeUnit) {
        LocalCache.put(key, value, timeout, timeUnit);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCacheObject(String key) {
        return (T) LocalCache.get(key);
    }

    @Override
    public boolean deleteObject(String key) {
        LocalCache.remove(key);
        return true;
    }

    @Override
    public boolean containsKey(String key) {
        return LocalCache.containsKey(key);
    }
}
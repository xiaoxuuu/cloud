package cc.xiaoxu.cloud.core.cache.local;

import cc.xiaoxu.cloud.exception.CustomException;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>自定义本地缓存工具类</p>
 *
 * @author 小徐
 * @since 2024/1/29 17:22
 */
@Slf4j
public class LocalCache {

    /**
     * 缓存数据Map
     */
    private static final Map<String, LocalCacheDTO> CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加缓存
     *
     * @param key    缓存键
     * @param value  缓存值
     */
    public static void put(String key, Object value) {

        put(key, value, null, null);
    }

    /**
     * 添加缓存
     *
     * @param key       缓存键
     * @param value     缓存值
     * @param timeout   过期时间
     * @param timeUnit  过期时间单位（毫秒及以上）
     */
    public static void put(String key, Object value, Long timeout, TimeUnit timeUnit) {

        LocalCacheDTO localCacheDTO = new LocalCacheDTO();
        localCacheDTO.setKey(key);
        localCacheDTO.setValue(value);
        if (Objects.nonNull(timeout) && Objects.nonNull(timeUnit)) {
            long millis = switch (timeUnit) {
                case MILLISECONDS -> Duration.ofMillis(timeout).toMillis();
                case SECONDS -> Duration.ofSeconds(timeout).toMillis();
                case MINUTES -> Duration.ofMinutes(timeout).toMillis();
                case HOURS -> Duration.ofHours(timeout).toMillis();
                case DAYS -> Duration.ofDays(timeout).toMillis();
                default -> throw new CustomException("错误的日期单位");
            };
            long expireTime = System.currentTimeMillis() + millis;
            localCacheDTO.setExpireTime(expireTime);
        }
        CACHE_MAP.put(key, localCacheDTO);
        log.debug("本地缓存增加了 key：{}", key);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 缓存数据
     */
    public static Object get(String key) {
        if (CACHE_MAP.containsKey(key)) {
            return CACHE_MAP.get(key).getValue();
        }
        return null;
    }

    /**
     * 移除缓存
     *
     * @param key 缓存键
     */
    public static void remove(String key) {
        CACHE_MAP.remove(key);
        log.debug("本地缓存删除了 key：{}", key);
    }

    /**
     * 清理过期的缓存数据
     */
    public static void clearCache() {

        if (CACHE_MAP.isEmpty()) {
            log.debug("本地缓存为空");
            return;
        }
        // 判断是否过期 过期就从缓存 Map 删除这个元素
        int size = CACHE_MAP.size();
        CACHE_MAP.entrySet().removeIf(entry -> entry.getValue().getExpireTime() != null && entry.getValue().getExpireTime() < System.currentTimeMillis());
        int sizeNew = CACHE_MAP.size();
        log.debug("清理本地缓存 {} 个，原大小：{}，现大小：{}", (size - sizeNew), size, sizeNew);
    }
}
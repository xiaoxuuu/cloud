package cc.xiaoxu.cloud.core.cache;

import java.util.concurrent.TimeUnit;

public interface CacheService {

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    <T> void setCacheObject(final String key, final T value);

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit);

    default <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit, boolean checkExists) {

        if (checkExists && containsKey(key)) {
            return;
        }
        setCacheObject(key, value, timeout, timeUnit);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    <T> T getCacheObject(final String key);

    /**
     * 删除单个对象
     *
     * @param key
     */
    boolean deleteObject(final String key);

    /**
     * 对象是否存在
     *
     * @param key key
     */
    boolean containsKey(final String key);
}
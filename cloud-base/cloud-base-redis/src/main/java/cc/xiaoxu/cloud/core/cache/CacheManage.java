package cc.xiaoxu.cloud.core.cache;

import cc.xiaoxu.cloud.core.constants.RedisConstants;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * RedisService 包装类，用于统一设置数据过期时间等
 * <p>
 * 2022/6/28 14:25
 *
 * @author XiaoXu
 */
@Component
public class CacheManage {

    private final Long defaultTime;
    private final TimeUnit defaultTimeUnit;
    private final CacheService cacheService;

    /**
     * 默认构造
     *
     * @param cacheService Spring 注入
     */
    @Autowired
    public CacheManage(CacheService cacheService) {
        this.cacheService = cacheService;
        this.defaultTime = 180L;
        this.defaultTimeUnit = TimeUnit.DAYS;
    }

    /**
     * 动态构造
     *
     * @param cacheService    redis 工具类
     * @param defaultTime     默认过期时间
     * @param defaultTimeUnit 默认过期单位
     */
    public CacheManage(CacheService cacheService, Long defaultTime, TimeUnit defaultTimeUnit) {
        this.cacheService = cacheService;
        this.defaultTime = defaultTime;
        this.defaultTimeUnit = defaultTimeUnit;
    }

    /**
     * 设置缓存
     *
     * @param key 缓存键值，传入非 String 类型会取调用方所在方法名 + MD5(key)为 redis key
     * @param t   数据
     * @return 缓存键值对应的数据
     */
    public <T> T setCache(Object key, T t) {

        return getOrSetCache(key, () -> defaultCall(t), true, defaultTime, defaultTimeUnit);
    }

    /**
     * 设置缓存
     *
     * @param key      缓存键值，传入非 String 类型会取调用方所在方法名 + MD5(key)为 redis key
     * @param callable 获取缓存方法
     * @return 缓存键值对应的数据
     */
    public <T> T setCache(Object key, Callable<T> callable) {

        return getOrSetCache(key, callable, true, defaultTime, defaultTimeUnit);
    }

    /**
     * 读取缓存，如果没有，调用方法获取数据并写缓存
     *
     * @param key      缓存键值，传入非 String 类型会取调用方所在方法名 + MD5(key)为 redis key
     * @param callable 获取缓存方法
     * @return 缓存键值对应的数据
     */
    public <T> T getCache(Object key, Callable<T> callable) {

        return getOrSetCache(key, callable, false, defaultTime, defaultTimeUnit);
    }

    /**
     * 读取缓存，如果没有，调用方法获取数据并写缓存
     *
     * @param key                   缓存键值，传入非 String 类型会取调用方所在方法名 + MD5(key)为 redis key
     * @param callable              获取缓存方法
     * @param forceRaedFromDatabase 强制从数据库读取
     * @param timeout               超时时间
     * @param timeUnit              超时时间单位
     * @return 缓存键值对应的数据
     */
    @SneakyThrows
    public <T> T getOrSetCache(Object key, Callable<T> callable, Boolean forceRaedFromDatabase, Long timeout, TimeUnit timeUnit) {

        // 读缓存
        String redisKey = RedisConstants.UNIFIED_CACHE + getRedisKey(key);
        if (!forceRaedFromDatabase) {
            String enableCache = cacheService.getCacheObject(RedisConstants.UNIFIED_CACHE + RedisConstants.ENABLE_CACHE);
            if ("yes".equals(enableCache)) {
                T cacheObject = cacheService.getCacheObject(redisKey);
                if (Objects.nonNull(cacheObject)) {
                    return cacheObject;
                }
            }
        }

        // 调用方法
        T call = callable.call();

        // 写缓存
        if (Objects.nonNull(call)) {
            cacheService.deleteObject(redisKey);
            cacheService.setCacheObject(redisKey, call, timeout, timeUnit);
        }
        return call;
    }

    /**
     * 用于直接返回数据
     *
     * @param t   数据
     * @param <T> 类型
     * @return 数据
     */
    private <T> T defaultCall(T t) {
        return t;
    }

    /**
     * 从给定类获取唯一 key
     *
     * @param key 类
     * @return 算出的 redisKey
     */
    private String getRedisKey(Object key) {

        if (key instanceof String) {
            return (String) key;
        }

        // 暂时使用请求参数的 MD5 数据作为 key
        String className = key.getClass().getSimpleName();
        return className + ":" + DigestUtils.md5DigestAsHex(JsonUtils.toString(key).getBytes()) + ":";
    }

    public void remove(String key) {

        cacheService.deleteObject(key);
    }
}
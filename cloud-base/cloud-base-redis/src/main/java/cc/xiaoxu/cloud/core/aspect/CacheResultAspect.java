package cc.xiaoxu.cloud.core.aspect;

import cc.xiaoxu.cloud.core.annotation.CacheResult;
import cc.xiaoxu.cloud.core.cache.redis.RedisService;
import cc.xiaoxu.cloud.core.constants.RedisConstants;
import cc.xiaoxu.cloud.core.util.RedisKeyUtil;
import cc.xiaoxu.cloud.core.utils.ServletUtils;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import cc.xiaoxu.cloud.core.utils.constants.SystemConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 缓存出参
 * <p>
 * 2022.03.23 上午 11:00
 *
 * @author XiaoXu
 */
@Aspect
@Component
public class CacheResultAspect {

    private static final Logger log = LoggerFactory.getLogger(CacheResultAspect.class);

    private final RedisService redisService;

    @Value("${spring.profiles.active}")
    private String active;

    public CacheResultAspect(RedisService redisService) {
        this.redisService = redisService;
    }

    /**
     * 对所有加了注解的 controller 进行拦截
     */
    @Pointcut("@annotation(cc.xiaoxu.cloud.core.annotation.CacheResult)")
    public void addAdvice() {
    }

    @Around("addAdvice()")
    public Object interceptor(ProceedingJoinPoint pjp) {

        // 接口返回值
        Object result = null;
        // 更新数据进入 redis
        boolean updateRedisData;

        HttpServletRequest request = ServletUtils.getRequest();
        String redisKey = RedisKeyUtil.getRedisKey(pjp, request, RedisConstants.CACHE_RESULT);

        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        // 获取注解
        CacheResult cacheResult = methodSignature.getMethod().getAnnotation(CacheResult.class);
        updateRedisData = cacheResult.renewal();

        // 读取缓存
        Object cacheObject = null;
        try {
            cacheObject = redisService.getCacheObject(redisKey);
        } catch (Exception e) {
            log.error("从缓存读取结果失败：{}", e.getMessage());
        }

        if (null == cacheObject) {
            // 缓存为空，调用接口获取数据，之后设置缓存
            try {
                result = pjp.proceed();
                updateRedisData = true;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            // 缓存不为空，直接返回结果
            if (cacheResult.list()) {
                result = JsonUtils.parseArray(cacheObject.toString(), cacheResult.clazz());
            } else {
                result = JsonUtils.parse(cacheObject.toString(), cacheResult.clazz());
            }
        }

        // 判断 dev 环境
        if (cacheResult.devSkip()) {
            if (SystemConstants.SPRING_PROFILES_ACTIVE_DEV.equals(active)) {
                updateRedisData = false;
            }
        }

        if (updateRedisData && Objects.nonNull(result)) {
            // 更新 redis 数据
            redisService.setCacheObject(redisKey, result, cacheResult.timeout(), cacheResult.timeUnit());
        }

        return result;
    }
}
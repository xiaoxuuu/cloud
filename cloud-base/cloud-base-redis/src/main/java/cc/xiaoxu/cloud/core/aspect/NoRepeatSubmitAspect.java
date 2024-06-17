package cc.xiaoxu.cloud.core.aspect;

import cc.xiaoxu.cloud.core.annotation.RepeatSubmit;
import cc.xiaoxu.cloud.core.bean.vo.REnum;
import cc.xiaoxu.cloud.core.cache.redis.RedisService;
import cc.xiaoxu.cloud.core.constants.RedisConstants;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.util.RedisKeyUtil;
import cc.xiaoxu.cloud.core.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>防止重复提交切面</p>
 *
 * @author 小徐
 * @since 2024/6/10 上午11:14
 */
@Aspect
@Component
@AllArgsConstructor
public class NoRepeatSubmitAspect {

    private final RedisService redisService;

    /**
     * 定义切点
     */
    @Pointcut("@annotation(cc.xiaoxu.cloud.core.annotation.RepeatSubmit)")
    public void preventDuplication() {
    }

    @Around("preventDuplication()")
    public Object around(ProceedingJoinPoint pjp) {

        HttpServletRequest request = ServletUtils.getRequest();
        String redisKey = RedisKeyUtil.getRedisKey(pjp, request, RedisConstants.REPEAT_SUBMIT_KEY);

        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        // 获取注解
        RepeatSubmit repeatSubmit = methodSignature.getMethod().getAnnotation(RepeatSubmit.class);

        // 这个值只是为了标记，不重要
        if (redisService.containsKey(redisKey)) {
            // 重复提交了抛出异常，如果是在项目中，根据具体情况处理。
            throw new CustomException(REnum.DUPLICATE_SUBMIT);
        }

        // 设置防重复操作限时标记（前置通知）
        redisService.setCacheObject(redisKey, "", (long) repeatSubmit.interval(), TimeUnit.MILLISECONDS);
        try {
            // 正常执行方法并返回
            return pjp.proceed();
        } catch (Throwable throwable) {
            // 确保方法执行异常实时释放限时标记(异常后置通知)
            redisService.deleteObject(redisKey);
            throw new RuntimeException(throwable);
        }
    }
}
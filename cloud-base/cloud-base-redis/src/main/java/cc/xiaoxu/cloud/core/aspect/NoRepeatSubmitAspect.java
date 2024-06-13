package cc.xiaoxu.cloud.core.aspect;

import cc.xiaoxu.cloud.core.annotation.RepeatSubmit;
import cc.xiaoxu.cloud.core.bean.vo.REnum;
import cc.xiaoxu.cloud.core.cache.redis.RedisService;
import cc.xiaoxu.cloud.core.constants.RedisConstants;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.util.RedisKeyUtil;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
    @Pointcut("@annotation(com.easy.framework.annotation.RepeatSubmit)")
    public void preventDuplication() {
    }

    @Around("preventDuplication()")
    public Object around(ProceedingJoinPoint joinPoint) {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = null;
        if (attributes != null) {
            request = attributes.getRequest();
        }
        // 获取执行方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 获取防重复提交注解
        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);

        String redisKey = RedisKeyUtil.getRedisKey(joinPoint, request, RedisConstants.REPEAT_SUBMIT_KEY);
        // 这个值只是为了标记，不重要
        if (!redisService.containsKey(redisKey)) {
            // 设置防重复操作限时标记（前置通知）
            redisService.setCacheObject(redisKey, "", (long) annotation.interval(), TimeUnit.MINUTES);
            try {
                // 正常执行方法并返回
                // ProceedingJoinPoint类型参数可以决定是否执行目标方法，
                // 且环绕通知必须要有返回值，返回值即为目标方法的返回值
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                // 确保方法执行异常实时释放限时标记(异常后置通知)
                redisService.deleteObject(redisKey);
                throw new RuntimeException(throwable);
            }
        } else {
            // 重复提交了抛出异常，如果是在项目中，根据具体情况处理。
            throw new CustomException(REnum.DUPLICATE_SUBMIT);
        }
    }

    /**
     * 生成方法标记：采用数字签名算法SHA1对方法签名字符串加签
     *
     * @param method 方法
     * @param args   参数
     * @return String
     */
    private String getMethodSign(Method method, Object... args) {
        StringBuilder sb = new StringBuilder(method.toString());
        for (Object arg : args) {
            sb.append(toString(arg));
        }
        return DigestUtils.md5DigestAsHex(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private String toString(Object arg) {
        if (Objects.isNull(arg)) {
            return "null";
        }
        if (arg instanceof Number) {
            return arg.toString();
        }
        return JsonUtils.toString(arg);
    }
}
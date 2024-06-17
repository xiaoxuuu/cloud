package cc.xiaoxu.cloud.core.util;

import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

public class RedisKeyUtil {

    /**
     * 获取 redisKey
     */
    public static String getRedisKey(ProceedingJoinPoint pjp, HttpServletRequest request, String redisPrefix) {
        String url = request.getRequestURI().replace("/", ":");
        if (url.startsWith(":")) {
            url = url.substring(1);
        }
        // 获取调用入参
        String requestString = JsonUtils.toString(pjp.getArgs());
        // 构建 redis key
        return redisPrefix + url + ":" + DigestUtils.md5DigestAsHex(requestString.getBytes(StandardCharsets.UTF_8));
    }
}
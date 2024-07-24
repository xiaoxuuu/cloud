package cc.xiaoxu.clond.core.satoken.exception;

import cc.xiaoxu.cloud.core.bean.vo.R;
import cn.dev33.satoken.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * <p>satoken异常处理</p>
 *
 * @author 小徐
 * @since 2024/7/24 上午11:47
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.easy")
public class GlobalExceptionHandler {

    /**
     * 登录异常拦截
     */
    @ExceptionHandler(value = NotLoginException.class)
    public R<String> handleNotLoginException(NotLoginException e) {
        log.error("satoken异常拦截-> e={}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

}
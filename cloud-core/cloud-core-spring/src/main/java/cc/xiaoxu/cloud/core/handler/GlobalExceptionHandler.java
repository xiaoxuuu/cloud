package cc.xiaoxu.cloud.core.handler;

import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.vo.R;
import cc.xiaoxu.cloud.core.utils.vo.REnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 运行时异常
     *
     * @param e 异常
     * @return message
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handle(RuntimeException e) {

        log.error("运行时异常拦截: {}", e.getMessage());
        e.printStackTrace();
        return R.fail(REnum.RUNTIME_EXCEPTION, e.getLocalizedMessage());
    }

    /**
     * 自定义异常
     *
     * @param e 异常
     * @return message
     */
    @ExceptionHandler(value = CustomException.class)
    public R<String> handleCustom(CustomException e) {

        log.error("自定义异常拦截: {}", e.getMessage());
        e.printStackTrace();
        return e.getResult();
    }

    /**
     * 参数校验异常拦截
     *
     * @param e 参数异常
     * @return message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

        // 从异常对象中拿到 ObjectError 对象
        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        log.error("参数校验异常拦截: {}", objectError.getDefaultMessage());
        // 然后提取错误提示信息进行返回
        return R.fail(REnum.PARAM_ERROR, objectError.getDefaultMessage());
    }
}
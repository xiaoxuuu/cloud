package cc.xiaoxu.cloud.core.exception;

import cc.xiaoxu.cloud.core.bean.vo.R;
import cc.xiaoxu.cloud.core.bean.vo.REnum;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CustomException extends RuntimeException {

    private final R<String> result;
    private static String error = "服务不可用，请稍后再试";

    public CustomException(REnum rEnum) {
        super(rEnum.getIntroduction());
        log.error("运行时异常拦截: {}", rEnum.getIntroduction());
        this.result = R.fail(rEnum.getCode(), error);
    }

    public CustomException(REnum rEnum, String msg) {
        super(msg);
        log.error("参数校验异常拦截: {}", msg);
        this.result = R.fail(rEnum.getCode(), error);
    }

    public CustomException(String message) {
        super(message);
        log.error("自定义异常拦截: {}", message);
        this.result = R.fail(error);
    }
}
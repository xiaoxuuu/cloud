package cc.xiaoxu.cloud.core.exception;

import cc.xiaoxu.cloud.core.bean.vo.R;
import cc.xiaoxu.cloud.core.bean.vo.REnum;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final R<String> result;

    public CustomException(REnum rEnum) {
        super(rEnum.getIntroduction());
        this.result = R.fail(rEnum);
    }

    public CustomException(REnum rEnum, String msg) {
        super(msg);
        this.result = R.fail(rEnum.getCode(), msg);
    }

    public CustomException(String message) {
        super(message);
        this.result = R.fail(message);
    }
}
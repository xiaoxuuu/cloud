package cc.xiaoxu.cloud.exception;

import cc.xiaoxu.cloud.bean.enums.REnum;
import cc.xiaoxu.cloud.bean.vo.R;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final R<String> result;

    public CustomException(REnum rEnum) {
        super(rEnum.getIntroduction());
        this.result = R.fail(rEnum);
    }

    public CustomException(String message) {
        super(message);
        this.result = R.fail(message);
    }
}
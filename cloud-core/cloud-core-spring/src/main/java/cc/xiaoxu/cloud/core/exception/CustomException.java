package cc.xiaoxu.cloud.core.exception;

import cc.xiaoxu.cloud.core.utils.vo.R;
import cc.xiaoxu.cloud.core.utils.vo.REnum;
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
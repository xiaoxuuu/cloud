package cc.xiaoxu.cloud.core.exception;

import cc.xiaoxu.cloud.core.bean.vo.R;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class CustomShowException extends RuntimeException {

    private final R<String> result;

    public CustomShowException(String message) {
        super(message);
        this.result = R.fail(message);
    }
}
package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ALiFileStatusEnum implements EnumInterface<String>, EnumDescInterface {

    INIT("init", "初始化状态，等待调度中。"),
    PARSING("parsing", "解析中"),
    PARSE_SUCCESS("success", "解析完成"),
    PARSE_FAILED("failed", "解析失败"),
    ;

    private final String code;
    private final String introduction;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
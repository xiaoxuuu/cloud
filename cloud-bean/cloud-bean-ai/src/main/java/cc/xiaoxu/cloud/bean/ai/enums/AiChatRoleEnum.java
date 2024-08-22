package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiChatRoleEnum implements EnumInterface<String>, EnumDescInterface {

    SYSTEM("system", "系统"),
    USER("user", "用户"),
    ASSISTANT("assistant", "ai"),
    ;

    private final String code;
    private final String introduction;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
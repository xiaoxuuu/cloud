package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiTalkTypeEnum implements EnumInterface<Integer> {

    KNOWLEDGE(1, "知识库提问"),

    TEST(99, "提示词测试"),
    ;

    private final Integer code;
    private final String introduction;
}
package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiModelTypeEnum implements EnumInterface<String> {

    LLM("llm", "语言模型"),
    EMBEDDING("embedding", "向量模型"),
    ;

    private final String code;
    private final String introduction;
}
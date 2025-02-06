package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiModelEnum implements EnumInterface<String>, EnumDescInterface {

    // KIMI
    MOONSHOT_V1_128K("moonshot-v1-128k", "KIMI 128K", AiTypeEnum.KIMI),

    // 本地
    LOCAL("DeepSeek-R1-Distill-Qwen-14B-GGUF", "本地 DeepSeek 14B 量化", AiTypeEnum.LOCAL),

    // DeepSeek
    D("deepseek-chat", "DeepSeek-V3", AiTypeEnum.LOCAL),
    ;

    private final String code;
    private final String introduction;
    private final AiTypeEnum type;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
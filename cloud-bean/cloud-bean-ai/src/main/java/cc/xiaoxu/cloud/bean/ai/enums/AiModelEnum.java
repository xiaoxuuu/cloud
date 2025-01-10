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
    LOCAL("qwen", "本地 千问 32B", AiTypeEnum.Q_WEN),
    LOCAL_QWEN2_5_32B_INSTRUCT_AWQ("Qwen2.5-32B-Instruct-AWQ", "本地 千问2.5 32B", AiTypeEnum.Q_WEN),
    LOCAL_QWEN2_5_14B_INSTRUCT_AWQ("Qwen2.5-14B-Instruct-AWQ", "本地 千问2.5 14B", AiTypeEnum.Q_WEN),

    // 测试
    TEST("test", "", AiTypeEnum.TEST),
    CUSTOM("custom", "", AiTypeEnum.TEST),
    ;

    private final String code;
    private final String introduction;
    private final AiTypeEnum type;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
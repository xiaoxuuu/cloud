package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiTypeEnum implements EnumInterface<Integer> {

    LOCAL(0, "本地模型", ""),
    KIMI(1, "KIMI", "https://api.moonshot.cn/v1"),
    Q_WEN(2, "通义千问", ""),
    DEEP_SEEK(3, "深度求索", "https://api.deepseek.com"),
    ;

    private final Integer code;
    private final String introduction;
    private final String url;
}
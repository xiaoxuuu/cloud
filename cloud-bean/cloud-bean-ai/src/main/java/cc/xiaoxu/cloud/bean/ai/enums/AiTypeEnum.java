package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiTypeEnum implements EnumInterface<Integer> {

    TEST(0, "测试", ""),
    KIMI(1, "KIMI", "https://api.moonshot.cn/v1"),
    Q_WEN(2, "通义千问", ""),
    ;

    private final Integer code;
    private final String introduction;
    private final String url;
}
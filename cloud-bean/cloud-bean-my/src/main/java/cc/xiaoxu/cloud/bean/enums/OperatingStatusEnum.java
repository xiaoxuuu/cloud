package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperatingStatusEnum implements EnumInterface<String> {

    OPEN("OPEN", "营业", true),
    PROCESSING("PROCESSING", "营业状态未知", true),
    DISSENT("DISSENT", "有争议", true),
    CLOSE("CLOSE", "已关闭", true),
    BELOW_STANDARD("BELOW_STANDARD", "不合标准", false),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
    private final Boolean show;
}
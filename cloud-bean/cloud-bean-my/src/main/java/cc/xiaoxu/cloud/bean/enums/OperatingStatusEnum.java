package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperatingStatusEnum implements EnumInterface<String> {

    OPEN("OPEN", "开业"),
    CLOSE("CLOSE", "关门"),
    RELOCATION("RELOCATION", "搬迁"),
    SUSPECTED_CLOSURE("SUSPECTED_CLOSURE", "疑似关闭"),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
}
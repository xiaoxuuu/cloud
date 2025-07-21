package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperatingStatusEnum implements EnumInterface<String> {

    OPEN("food", "开业"),
    CLOSE("fun_name", "关门"),
    RELOCATION("relocation", "搬迁"),
    SUSPECTED_CLOSURE("suspected_closure", "疑似关闭"),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
}
package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OperatingStatusEnum implements EnumInterface<String> {

    OPEN("OPEN", "营业"),
    CLOSE("CLOSE", "关闭"),
    ING("ING", "待核实"),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
}
package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StateEnum implements EnumInterface<String>, EnumDescInterface {

    /**
     * 数据状态
     */
    ENABLE("E", "启用"),
    DELETE("D", "删除"),
    LOCK("L", "禁用"),
    PROGRESSING("P", "处理中"),
    ;

    @EnumValue
    private final String code;
    private final String introduction;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
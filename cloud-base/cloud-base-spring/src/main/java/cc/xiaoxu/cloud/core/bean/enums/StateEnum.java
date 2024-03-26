package cc.xiaoxu.cloud.core.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StateEnum implements EnumInterface<String> {

    /**
     * 数据状态
     */
    ENABLE("E", "启用"),
    DELETE("D", "删除"),
    LOCK("L", "禁用"),
    AUDITING("T", "审核");

    private final String code;
    private final String introduction;
}
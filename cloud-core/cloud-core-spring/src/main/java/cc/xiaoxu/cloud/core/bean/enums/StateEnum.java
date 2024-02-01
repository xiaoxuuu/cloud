package cc.xiaoxu.cloud.core.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StateEnum implements EnumInterface<String> {

    /**
     * 启用 E；删除 D；禁用 L；审核中 T；
     */
    E("E", "启用"),
    D("D", "删除"),
    L("L", "禁用"),
    T("T", "审核");

    private final String code;
    private final String introduction;
}
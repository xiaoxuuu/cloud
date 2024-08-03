package cc.xiaoxu.cloud.my.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IconTypeEnum implements EnumInterface<String> {

    /**
     *
     */
    SVG("S", ""),
    BASE64("B", ""),
    NULL("N", ""),
    ;

    private final String code;
    private final String introduction;
}
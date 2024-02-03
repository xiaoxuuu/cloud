package cc.xiaoxu.cloud.my.navigation.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.EnumInterface;
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
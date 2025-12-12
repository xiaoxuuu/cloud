package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MapTypeEnum implements EnumInterface<String> {

    /**
     *
     */
    AMAP("AMAP", "高德地图"),
    ;

    private final String code;
    private final String introduction;
}
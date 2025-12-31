package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointSourceTypeEnum implements EnumInterface<String> {

    FRIEND("FRIEND", "朋友", 10000),
    AUTHOR("AUTHOR", "作者   ", 10000),

    TIKTOK("TIKTOK", "抖音", 1000),
    BILIBILI("BILIBILI", "bilibili", 1000),
    RED_BOOK("RED_BOOK", "小红书", 1000),

    AMAP("AMAP", "高德", 100),

    AI("AI", "ai", 10),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
    private final Integer sortingWeight;
}
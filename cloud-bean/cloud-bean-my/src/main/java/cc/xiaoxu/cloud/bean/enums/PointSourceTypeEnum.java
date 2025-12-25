package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointSourceTypeEnum implements EnumInterface<String> {

    FRIEND("friend", "朋友", 10000),
    AUTHOR("author", "作者   ", 10000),

    TIKTOK("tiktok", "抖音", 1000),
    BILIBILI("bilibili", "bilibili", 1000),
    RED_BOOK("red_book", "小红书", 1000),

    AMAP("amap", "高德", 100),

    AI("ai", "ai", 10),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
    private final Integer sortingWeight;
}
package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointSourceTypeEnum implements EnumInterface<String> {

    RANK_NOODLE_CD_50("RANK_NOODLE_CD_50", "成都面馆50强", 100),

    TIKTOK("TIKTOK", "抖音", 2000),
    BILIBILI("BILIBILI", "bilibili", 2000),
    RED_BOOK("RED_BOOK", "小红书", 2000),

    AUTHOR("AUTHOR", "作者", 3000),

    INTERNET("INTERNET", "互联网", 10000),

//    AMAP("AMAP", "高德", 100),
//    AI("AI", "ai", 10),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
    private final Integer sortingWeight;
}
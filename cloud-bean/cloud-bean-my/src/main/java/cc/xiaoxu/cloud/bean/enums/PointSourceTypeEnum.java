package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointSourceTypeEnum implements EnumInterface<String> {

    FRIEND("friend", "朋友"),
    TIKTOK("tiktok", "抖音"),
    BILIBILI("bilibili", "bilibili"),
    RED_BOOK("red_book", "小红书"),
    AMAP("amap", "高德"),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
}
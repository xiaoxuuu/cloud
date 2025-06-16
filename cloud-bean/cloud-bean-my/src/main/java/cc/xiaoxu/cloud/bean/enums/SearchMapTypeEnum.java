package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SearchMapTypeEnum implements EnumInterface<String> {

    EXISTS_DATA("exists_data", "已有数据"),
    AMAP_POI("amap_poi", "高德 POI 数据"),
    AMAP_INPUT("amap_input", "高德搜索提示"),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
}
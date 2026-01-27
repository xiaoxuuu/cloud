package cc.xiaoxu.cloud.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointTypeEnum implements EnumInterface<String> {

    FOOD("food", "美食"),
    FUN_NAME("fun_name", "有趣地名"),
    SCENERY("scenery", "美景"),
    UNCATEGORIZED("uncategorized", "未分类"),

    AGGREGATION("aggregation", "聚合点位"),
    DISTRICT("district", "区县"),
    CITY("city", "市"),
    PROVINCE("province", "省"),
    ;

    @EnumValue
    private final String code;
    private final String introduction;
}
package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 全参数 - 响应参数")
public class PointFullVO extends PointShowVO {

    @Schema(description = "id")
    private Integer id;

    @Schema(description = "上级id，用于数据归总")
    private Integer parentId;

    @Schema(description = "收藏次数")
    private Integer collectTimes;

    @Schema(description = "照片")
    private String photo;

    @Schema(description = "我去过的次数")
    private Integer visitedTimes;

    @Schema(description = "地址code")
    private Integer addressCode;

    @Schema(description = "标签id集合")
    private String tagIdList;

    @Schema(description = "标签")
    private Set<Integer> tagIdSet;

    @Schema(description = "来源id集合")
    private String sourceIdList;

    @Schema(description = "来源")
    private Set<Integer> sourceIdSet;

    @Schema(description = "高德地图id")
    private String amapId;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "模糊匹配字段")
    private String searchValue;
}
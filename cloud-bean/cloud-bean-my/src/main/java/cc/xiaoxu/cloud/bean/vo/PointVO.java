package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 业务参数 - 响应参数")
public class PointVO extends PointSimpleVO {

    @Schema(description = "全称")
    private String pointFullName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "详细地址")
    private String address;

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

    @Schema(description = "推荐距离(米）")
    private Integer recommendedDistance;

    @Schema(description = "营业时间")
    private String openingHours;

    @Schema(description = "联系电话")
    private String telephone;

    @Schema(description = "人均（分）")
    private Integer cost;

    @Schema(description = "标签id集合")
    private String tagIdList;

    @Schema(description = "描述")
    private String remark;

    @Schema(description = "更新时间")
    private Date modifyTime;

    // TODO 需移除
    @Schema(description = "高德地图id")
    private String amapId;
}
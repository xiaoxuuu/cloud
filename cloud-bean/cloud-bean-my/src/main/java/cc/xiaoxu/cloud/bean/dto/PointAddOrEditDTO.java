package cc.xiaoxu.cloud.bean.dto;

import cc.xiaoxu.cloud.bean.enums.OperatingStatusEnum;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 新增或编辑 - 请求参数")
public class PointAddOrEditDTO {

    @Schema(description = "数据id")
    private Integer id;

    @Schema(description = "点位类型")
    private PointTypeEnum pointType;

    @Schema(description = "简称")
    private String pointShortName;

    @Schema(description = "全称")
    private String pointFullName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "上级id，用于数据归总")
    private Integer parentId;

    @Schema(description = "照片")
    private String photo;

    @Schema(description = "我去过的次数")
    private Integer visitedTimes;

    @Schema(description = "地址code")
    private Integer addressCode;

    @Schema(description = "营业状态")
    private OperatingStatusEnum operatingStatus;

    @Schema(description = "推荐距离(米）")
    private Integer recommendedDistance;

    @Schema(description = "营业时间")
    private String openingHours;

    @Schema(description = "联系电话")
    private String telephone;

    @Schema(description = "人均（分）")
    private Integer cost;

    @Schema(description = "备注")
    private String remark;
}
package cc.xiaoxu.cloud.bean.vo;

import cc.xiaoxu.cloud.bean.enums.OperatingStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 业务参数 - 响应参数")
public class PointVO extends PointSimpleVO {

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
    private String addressCode;

    @Schema(description = "营业状态")
    private OperatingStatusEnum operatingStatus;
}
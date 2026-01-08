package cc.xiaoxu.cloud.bean.dto;

import cc.xiaoxu.cloud.bean.enums.OperatingStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 搜索 - 请求参数")
public class PointSearchDTO {

    @Schema(description = "地图缩放层级", requiredMode = Schema.RequiredMode.REQUIRED)
    private String scale;

    @Schema(description = "中心点纬度", requiredMode = Schema.RequiredMode.REQUIRED)
    private String centerLatitude;

    @Schema(description = "中心点纬度", requiredMode = Schema.RequiredMode.REQUIRED)
    private String centerLongitude;

    @Schema(description = "点位名称")
    private String pointName;

    @Schema(description = "营业状态")
    private Set<OperatingStatusEnum> operatingStatusSet;

    @Schema(description = "作者筛选")
    private Set<Integer> authorIdSet;
}
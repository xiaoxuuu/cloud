package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 新增 - 请求参数")
public class PointAddDTO {

    @Schema(description = "简称")
    private String pointShortName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "高德地图ID")
    private String amapId;
}
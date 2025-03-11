package cc.xiaoxu.cloud.bean.vo;

import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 精简参数 - 响应参数")
public class PointSimpleVO {

    @Schema(description = "点位类型")
    private PointTypeEnum pointType;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;
}
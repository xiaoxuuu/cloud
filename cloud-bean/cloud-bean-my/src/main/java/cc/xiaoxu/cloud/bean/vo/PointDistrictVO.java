package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 区县参数 - 响应参数")
public class PointDistrictVO extends PointSimpleVO {

    @Schema(description = "经度")
    private String nextLongitude;

    @Schema(description = "纬度")
    private String nextLatitude;
}
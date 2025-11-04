package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - id查询 - 请求参数")
public class PointGetDTO extends IdDTO {

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;
}
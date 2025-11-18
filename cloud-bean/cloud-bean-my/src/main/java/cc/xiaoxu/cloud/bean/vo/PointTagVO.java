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
@Schema(description = "区划 - 响应参数")
public class PointTagVO extends BaseIdVO{

    @Schema(description = "标签名")
    private String tagName;

    @Schema(description = "颜色")
    private String color;
}
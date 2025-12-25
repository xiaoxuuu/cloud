package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位标签 - 展示 - 响应参数")
public class PointTagShowVO {

    @Schema(description = "标签名")
    private String tagName;

    @Schema(description = "颜色")
    private String color;
}
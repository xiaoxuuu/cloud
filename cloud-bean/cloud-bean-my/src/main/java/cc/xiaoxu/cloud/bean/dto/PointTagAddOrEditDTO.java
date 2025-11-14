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
@Schema(description = "点位标签 - 新增或编辑 - 请求参数")
public class PointTagAddOrEditDTO extends IdDTO {

    @Schema(description = "标签名")
    private String tagName;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "状态")
    private String state;
}
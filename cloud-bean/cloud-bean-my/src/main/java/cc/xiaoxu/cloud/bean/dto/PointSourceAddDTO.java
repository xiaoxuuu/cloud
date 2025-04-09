package cc.xiaoxu.cloud.bean.dto;

import cc.xiaoxu.cloud.bean.enums.PointSourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位来源 - 新增 - 请求参数")
public class PointSourceAddDTO {

    @Schema(description = "类型")
    private PointSourceTypeEnum type;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "链接")
    private String url;
}
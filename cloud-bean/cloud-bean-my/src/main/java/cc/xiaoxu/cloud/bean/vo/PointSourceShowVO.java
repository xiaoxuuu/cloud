package cc.xiaoxu.cloud.bean.vo;

import cc.xiaoxu.cloud.bean.enums.PointSourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位来源 - 展示 - 响应参数")
public class PointSourceShowVO {

    @Schema(description = "作者名称")
    private String authorName;

    @Schema(description = "类型")
    private PointSourceTypeEnum type;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "链接")
    private String url;
}
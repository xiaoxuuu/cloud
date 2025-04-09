package cc.xiaoxu.cloud.bean.vo;

import cc.xiaoxu.cloud.bean.enums.PointSourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位来源 - 业务参数 - 响应参数")
public class PointSourceVO extends BaseIdVO {

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
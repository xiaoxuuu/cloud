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
@Schema(description = "点位来源作者 - 响应参数")
public class PointSourceAuthorVO extends BaseIdVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "抖音链接")
    private String tiktokUrl;

    @Schema(description = "小红书链接")
    private String redbookUrl;

    @Schema(description = "B站链接")
    private String bilibiliUrl;

    @Schema(description = "内容")
    private String content;
}
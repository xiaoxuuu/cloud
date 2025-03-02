package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsiteVO extends BaseVO {

    @Schema(description = "展示名称")
    private String shortName;

    @Schema(description = "网站名称（抓取）")
    private String websiteName;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "网站描述")
    private String description;

    @Schema(description = "上次正常访问时间（抓取）")
    private String lastAvailableTime;

    @Schema(description = "访问次数")
    private String visitNum;

    @Schema(description = "排序权重")
    private String sort;
}
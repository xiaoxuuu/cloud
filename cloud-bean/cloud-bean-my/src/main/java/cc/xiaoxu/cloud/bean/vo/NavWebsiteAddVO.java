package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsiteAddVO {

    @Schema(description = "展示名称")
    private String shortName;

    @Schema(description = "网站名称")
    private String websiteName;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图标id")
    private Integer iconId;
}
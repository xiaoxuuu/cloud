package cc.xiaoxu.cloud.my.nav.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsiteShowVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "展示名称")
    private String shortName;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "网站描述")
    private String description;

    @Schema(description = "标签")
    private Set<String> labelSet;

    @Schema(description = "类型")
    private Set<String> typeSet;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "图标类型：BASE64, SVG")
    private String iconType;

    @Schema(description = "访问次数")
    private Integer visitNum;

    @Schema(description = "上次访问时间")
    private String lastVisitDesc;
}
package cc.xiaoxu.cloud.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsiteShowVO {

    @Schema(description = "id")
    private Integer id;

    @Schema(description = "展示名称")
    private String shortName;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "网站描述")
    private String description;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "图标类型：BASE64, SVG")
    private String iconType;

    @Schema(description = "访问次数")
    private Integer visitNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "上次正常访问时间（抓取）")
    private Date lastAvailableTime;

    @Schema(description = "上次访问时间")
    private String lastVisitDesc;
}
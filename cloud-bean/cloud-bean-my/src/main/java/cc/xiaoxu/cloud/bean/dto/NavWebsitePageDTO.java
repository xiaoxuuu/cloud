package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsitePageDTO extends PageDTO {

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "标签（暂未实现）")
    private List<String> labelList;

    @Schema(description = "类型（暂未实现）")
    private String type;

    @Schema(description = "访问次数")
    private String visitNum;

    @Schema(description = "是否存在图标")
    private Boolean haveIcon;

    @Schema(description = "上次正常访问时间（抓取）")
    private String lastAvailableTimeStart;

    @Schema(description = "上次正常访问时间（抓取）")
    private String lastAvailableTimeEnd;
}
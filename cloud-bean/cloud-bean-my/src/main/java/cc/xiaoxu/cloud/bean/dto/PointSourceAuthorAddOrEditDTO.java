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
@Schema(description = "点位来源作者 - 新增或编辑 - 请求参数")
public class PointSourceAuthorAddOrEditDTO extends IdDTO {

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

//    @Schema(description = "状态")
//    private String state;

    @Schema(description = "备注")
    private String remark;
}
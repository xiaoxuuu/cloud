package cc.xiaoxu.cloud.my.nav.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsiteSearchVO {

    @Schema(description = "关键字")
    private String keyword;

    @Schema(description = "标签（暂未实现）")
    private String label;

    @Schema(description = "类型（暂未实现）")
    private String type;
}
package cc.xiaoxu.cloud.bean.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Schema(description = "Web 搜索信息")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class WebSearchInfoDTO {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "内容")
    private String content;

    @JsonAlias("raw_content")
    @Schema(description = "清洗并解析的搜索结果 HTML 内容。仅当 include_raw_content 为真时。")
    private String rawContent;

    @Schema(description = "分数")
    private Float score;
}
package cc.xiaoxu.cloud.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Schema(description = "Web 提取 成功结果")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class WebExtractResultDTO {

    @Schema(description = "链接")
    private String url;

    @JsonAlias("raw_content")
    @Schema(description = "内容")
    private String rawContent;

    @Schema(description = "提取的图像 URL 列表。")
    private List<String> images;
}
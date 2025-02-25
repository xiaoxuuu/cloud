package cc.xiaoxu.cloud.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Schema(description = "Web 提取 失败结果")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class WebExtractErrorDTO {

    @Schema(description = "链接")
    private String url;

    @Schema(description = "失败原因")
    private String error;
}
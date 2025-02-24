package cc.xiaoxu.cloud.bean.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Schema(description = "Web 提取 ")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class WebExtractDTO {

    @Schema(description = "成功结果")
    private List<WebExtractResultDTO> results;

    @JsonAlias("failed_results")
    @Schema(description = "失败结果")
    private List<WebExtractErrorDTO> failedResults;

    @JsonAlias("response_time")
    @Schema(description = "请求完成所需时间（秒）")
    private Float responseTime;
}
package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "AI回答的内容体")
@Accessors(chain = true)
@AllArgsConstructor
public class AiChatResultDTO {

    @Schema(description = "Ai回答的文本")
    private String result;

    @Schema(description = "消耗的Token")
    private Integer token;

    @Schema(description = "耗时")
    private Integer summaryDuration;

    @Schema(description = "响应 code")
    private Integer statusCode;

    @Schema(description = "异常信息")
    private String errorMsg;

    public AiChatResultDTO() {
        this.token = 0;
    }
}
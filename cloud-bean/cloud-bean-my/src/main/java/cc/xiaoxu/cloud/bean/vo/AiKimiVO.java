package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiKimiVO {

    @Schema(description = "apiKey")
    private String apiKey;

    @Schema(description = "问题")
    private String question;

    @Schema(description = "会话id")
    private String talkId;
}
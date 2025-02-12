package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeAddLocalFileEventDTO {

    @Schema(description = "知识库id")
    private Integer knowledgeId;

    @Schema(description = "用户")
    private Integer userId;
}
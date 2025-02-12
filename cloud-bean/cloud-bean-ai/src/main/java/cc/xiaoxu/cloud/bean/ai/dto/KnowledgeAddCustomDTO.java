package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeAddCustomDTO {

    @Schema(description = "知识库id")
    private Integer knowledgeBaseId;

    @Schema(description = "知识库名称")
    private String knowledgeName;

    @Schema(description = "内容")
    private String content;
}
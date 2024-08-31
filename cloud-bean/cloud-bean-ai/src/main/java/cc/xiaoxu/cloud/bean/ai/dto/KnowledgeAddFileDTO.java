package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeAddFileDTO {

    @Schema(description = "分类 id")
    private String categoryId;

    @Schema(description = "工作空间 id")
    private String workspaceId;
}
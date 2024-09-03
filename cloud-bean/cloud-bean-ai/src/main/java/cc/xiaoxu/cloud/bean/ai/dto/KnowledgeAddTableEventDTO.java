package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeAddTableEventDTO {

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "数据查询语句")
    private String sql;

    @Schema(description = "知识库id")
    private Integer knowledgeId;
}
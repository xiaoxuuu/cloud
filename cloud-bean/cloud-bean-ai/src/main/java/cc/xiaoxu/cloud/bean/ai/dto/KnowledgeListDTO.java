
package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识 - 列表查询 - 请求参数")
public class KnowledgeListDTO {

    @Schema(description = "知识库id")
    private Integer knowledgeBaseId;
}
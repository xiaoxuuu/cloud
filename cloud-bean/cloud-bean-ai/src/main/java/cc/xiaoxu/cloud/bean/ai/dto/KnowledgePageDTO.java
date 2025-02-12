package cc.xiaoxu.cloud.bean.ai.dto;

import cc.xiaoxu.cloud.bean.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识 - 分页查询 - 请求参数")
public class KnowledgePageDTO extends PageDTO {

    @Schema(description = "知识库id")
    private Integer knowledgeBaseId;

    @Schema(description = "名称")
    private String name;
}
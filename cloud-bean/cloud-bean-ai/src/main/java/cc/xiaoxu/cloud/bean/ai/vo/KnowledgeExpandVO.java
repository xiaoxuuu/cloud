package cc.xiaoxu.cloud.bean.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeExpandVO extends KnowledgeVO {

    @Schema(description = "资源类型名称")
    private String typeName;

    @Schema(description = "资源处理状态名称")
    private String statusName;
}
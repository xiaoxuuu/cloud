package cc.xiaoxu.cloud.bean.ai.dto;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话信息 - 编辑")
public class ConversationEditDTO extends IdDTO {

    @Schema(description = "名称，不传则不修改")
    private String name;

    @Schema(description = "模型id，不传则不修改")
    private Integer modelId;
}
package cc.xiaoxu.cloud.bean.ai.dto;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.doc.annotation.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话信息 - 编辑知识库关系")
public class ConversationEditKnowledgeBaseDTO extends IdDTO {

    @NotBlank(message = "请选择知识库")
    @Schema(description = "知识库id")
    private Integer knowledgeBaseId;

    @Schema(description = "状态", allowableValues = {"ENABLE", "DELETE"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @SchemaEnum(clazz = StateEnum.class)
    private String state;
}
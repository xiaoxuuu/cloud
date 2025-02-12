package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话 - 新增 - 请求参数")
public class ConversationAddDTO {

    @NotBlank(message = "问题不能为空")
    @Schema(description = "问题")
    private String question;

    @NotBlank(message = "请选择模型")
    @Schema(description = "模型id")
    private Integer modelId;

    @NotBlank(message = "请选择知识库")
    @Schema(description = "知识库id")
    private Integer knowledgeBaseId;

    @Schema(description = "对话id，留空则新建对话")
    private Integer conversationId;
}
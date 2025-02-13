package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话 - 新增 - 请求参数")
public class ConversationAddDTO {

    @NotBlank(message = "问题不能为空")
    @Schema(description = "问题")
    private String question;

    @Schema(description = "对话id，留空则新建对话")
    private Integer conversationId;

    @Schema(description = "模型id，若未传入 conversationId 此参数必填")
    private Integer modelId;

    @Schema(description = "知识库id，若未传入 conversationId 此参数必填")
    private List<Integer> knowledgeBaseIdList;
}
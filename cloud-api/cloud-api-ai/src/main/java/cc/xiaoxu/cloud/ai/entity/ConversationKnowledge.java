package cc.xiaoxu.cloud.ai.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntityForPostgres;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("r_conversation_knowledge")
@Schema(description = "关系 - 会话知识库引用")
public class ConversationKnowledge extends BaseEntityForPostgres {

    @Schema(description = "名称")
    private Integer conversationId;

    @Schema(description = "知识库id")
    private Integer knowledgeBaseId;
}
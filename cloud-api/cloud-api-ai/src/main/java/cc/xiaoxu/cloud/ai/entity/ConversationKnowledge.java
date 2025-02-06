package cc.xiaoxu.cloud.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ConversationKnowledge {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键id")
    private Integer id;

    @Schema(description = "名称")
    private Integer conversationId;

    @Schema(description = "用户id")
    private Integer knowledgeId;
}
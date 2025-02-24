package cc.xiaoxu.cloud.ai.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
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
@TableName("t_knowledge_section")
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSection extends BaseEntity {

    @Schema(description = "知识库id")
    private Integer knowledgeBaseId;

    @Schema(description = "知识id")
    private Integer knowledgeId;

    @Schema(description = "用户id")
    private Integer userId;

    @Schema(description = "知识切片内容")
    private String cutContent;

    @Schema(description = "知识切片向量，1024 维")
    private String embedding;
}
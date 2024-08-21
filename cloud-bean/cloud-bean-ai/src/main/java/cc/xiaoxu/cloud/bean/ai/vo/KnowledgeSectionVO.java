package cc.xiaoxu.cloud.bean.ai.vo;

import cc.xiaoxu.cloud.bean.vo.BaseVO;
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
public class KnowledgeSectionVO extends BaseVO {

    @Schema(description = "知识id")
    private Integer knowledgeId;

    @Schema(description = "知识切片内容")
    private String cutContent;

    @Schema(description = "知识切片向量，1536 维")
    private String embedding;

    @Schema(description = "距离")
    private String distance;
}
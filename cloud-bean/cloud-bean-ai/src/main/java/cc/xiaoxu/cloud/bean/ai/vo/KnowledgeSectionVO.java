package cc.xiaoxu.cloud.bean.ai.vo;

import cc.xiaoxu.cloud.bean.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识切片")
public class KnowledgeSectionVO extends BaseVO {

    @Schema(description = "知识id")
    private Integer knowledgeId;

    @Schema(description = "知识切片内容")
    private String cutContent;

    @Schema(description = "知识切片向量，1024 维")
    private String embedding;
}
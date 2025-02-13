package cc.xiaoxu.cloud.bean.ai.vo;

import cc.xiaoxu.cloud.bean.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话信息")
public class ConversationVO extends BaseVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "模型id")
    private Integer modelId;

    @Schema(description = "知识库id集合")
    private List<Integer> knowledgeBaseIdList;
}
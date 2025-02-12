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
@Schema(description = "知识库 - 详情 - 请求参数")
public class KnowledgeBaseVO extends BaseVO {

    @Schema(description = "名称")
    private String name;
}
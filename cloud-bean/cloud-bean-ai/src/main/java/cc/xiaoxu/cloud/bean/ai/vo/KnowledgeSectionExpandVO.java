package cc.xiaoxu.cloud.bean.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSectionExpandVO extends KnowledgeSectionVO {

    @Schema(description = "距离")
    private String distance;
}
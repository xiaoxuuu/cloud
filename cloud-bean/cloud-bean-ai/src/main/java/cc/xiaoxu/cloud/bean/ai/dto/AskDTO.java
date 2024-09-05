package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AskDTO {

    @Schema(description = "问题", example = "袁茵是谁")
    private String question;

    @Schema(description = "相似度，越小越好，越大越不相似", example = "0.7")
    private Double similarity;

    @Schema(description = "引用分段数，取最相似的前 n 条", example = "5")
    private Integer similarityContentNum;

    @Schema(description = "选用知识分类，留空则不限制")
    private String knowledgeId;
}
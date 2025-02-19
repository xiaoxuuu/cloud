package cc.xiaoxu.cloud.bean.ai.dto;

import cc.xiaoxu.cloud.bean.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "对话详情 - 分页 - 请求参数")
public class ConversationDetailPageDTO extends PageDTO {

    @Schema(description = "对话id")
    private String conversionId;
}
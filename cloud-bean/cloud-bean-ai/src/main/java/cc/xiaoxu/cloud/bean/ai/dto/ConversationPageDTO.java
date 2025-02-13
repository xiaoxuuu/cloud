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
@Schema(description = "对话 - 分页 - 请求参数")
public class ConversationPageDTO extends PageDTO {

    @Schema(description = "名称")
    private String name;
}
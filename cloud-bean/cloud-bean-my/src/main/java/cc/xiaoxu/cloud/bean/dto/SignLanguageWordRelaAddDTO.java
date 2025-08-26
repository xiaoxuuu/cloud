package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "手语词汇关系 - 新增 - 请求参数")
public class SignLanguageWordRelaAddDTO {

    @Schema(description = "词汇1")
    private Integer wordIdLeft;

    @Schema(description = "词汇2")
    private Integer wordIdRight;
}
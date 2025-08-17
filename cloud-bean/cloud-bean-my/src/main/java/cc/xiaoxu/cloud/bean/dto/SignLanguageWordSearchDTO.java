package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "手语词汇 - 地图搜索 - 请求参数")
public class SignLanguageWordSearchDTO {

    @Schema(description = "搜索关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keywords;
}
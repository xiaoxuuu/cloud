package cc.xiaoxu.cloud.bean.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "模型配置")
public class ModelInfoVO {

    @Schema(description = "模型公司")
    private String company;

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "模型类型")
    private String model;
}
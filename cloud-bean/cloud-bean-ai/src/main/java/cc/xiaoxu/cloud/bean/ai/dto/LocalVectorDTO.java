package cc.xiaoxu.cloud.bean.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Schema(description = "本地向量模型响应")
@Accessors(chain = true)
@AllArgsConstructor
public class LocalVectorDTO {

    @Schema(description = "顺序索引")
    private Integer index;

    @Schema(description = "向量")
    private List<Float> embedding;
}

package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "区划 - 响应参数")
public class AreaTreeVO {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "名称")
    private String code;

    @Schema(description = "下级")
    private List<AreaTreeVO> childrenList;
}
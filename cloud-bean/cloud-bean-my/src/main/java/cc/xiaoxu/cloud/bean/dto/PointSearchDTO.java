package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 搜索 - 请求参数")
public class PointSearchDTO {

    @Schema(description = "点位类型")
    private List<String> pointType;

    @Schema(description = "点位名称")
    private String pointName;

    @Schema(description = "缩放层级")
    private Double level;

    @Schema(description = "状态")
    private List<String> stateList;
}
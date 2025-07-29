package cc.xiaoxu.cloud.bean.dto;

import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
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
    private List<PointTypeEnum> pointType;

    @Schema(description = "点位名称")
    private String pointName;

    @Schema(description = "缩放层级")
    private Double level;

    @Schema(description = "作者去过")
    private Boolean visit;
}
package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 全参数 - 响应参数")
public class PointExpandVO extends PointFullVO {

    @Schema(description = "来源")
    private List<PointSourceVO> source;
}
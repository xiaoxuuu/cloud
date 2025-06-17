package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 地图搜索 - 响应参数")
public class PointMapSearchVO {

    @Schema(description = "此位置在地图中的唯一 ID")
    private String mapId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "POI类型")
    private String type;

    @Schema(description = "地址信息")
    private PointMapSearchAddressVO addressVO;

    @Schema(description = "营业信息")
    private PointMapSearchBusinessVO businessVO;
}
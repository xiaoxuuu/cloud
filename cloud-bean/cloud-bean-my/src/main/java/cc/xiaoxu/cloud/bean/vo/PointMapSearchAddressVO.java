package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 地图搜索 - 地址信息 - 响应参数")
public class PointMapSearchAddressVO {

    @Schema(description = "坐标点，格式：经度,纬度")
    private String location;

    @Schema(description = "POI所在省份名称")
    private String province;

    @Schema(description = "POI所在城市名称")
    private String city;

    @Schema(description = "所在区县名称")
    private String district;

    @Schema(description = "所在区县编码")
    private String districtCode;

    @Schema(description = "地址")
    private String address;
}
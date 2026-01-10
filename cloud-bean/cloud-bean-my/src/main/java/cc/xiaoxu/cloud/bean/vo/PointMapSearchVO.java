package cc.xiaoxu.cloud.bean.vo;

import cc.xiaoxu.cloud.bean.enums.SearchMapTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 地图搜索 - 响应参数")
public class PointMapSearchVO {

    @Schema(description = "搜索来源")
    private SearchMapTypeEnum searchMapType;

    @Schema(description = "此位置在地图中的唯一 ID")
    private String mapId;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "坐标点，格式：经度,纬度")
    private String location;

    @Schema(description = "POI所在省份名称")
    private String province;

    @Schema(description = "POI所在城市名称")
    private String city;

    @Schema(description = "所在区县名称")
    private String district;

    @Schema(description = "所在区县编码")
    private Integer districtCode;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "营业时间")
    private String openingHours;

    @Schema(description = "人均（分）")
    private Integer cost;

    @Schema(description = "标签集合")
    private Set<String>  tagSet;
}
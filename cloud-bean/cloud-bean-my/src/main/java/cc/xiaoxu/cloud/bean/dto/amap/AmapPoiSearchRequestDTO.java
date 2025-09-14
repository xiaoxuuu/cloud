package cc.xiaoxu.cloud.bean.dto.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德地图搜索POI 2.0 - 请求参数")
public class AmapPoiSearchRequestDTO extends AmapBaseRequestDTO {

    @Schema(description = "每页记录数据，强烈建议不超过25，最大值为50")
    private Integer pageSize = 20;

    @Schema(description = "当前页数，最大翻页数100")
    private Integer pageNum = 1;

    @Schema(description = "返回结果详细程度，可选值：base-返回基本信息；all-返回全部信息")
    private String extensions = "base";

    @Schema(description = "POI搜索类型，可选值：0-普通搜索；1-分类搜索")
    private String type = "0";

    @Schema(description = "显示字段控制，可选值：children,business,indoor,navi,photos")
    private String showFields = "children,business,indoor,navi,photos";
}
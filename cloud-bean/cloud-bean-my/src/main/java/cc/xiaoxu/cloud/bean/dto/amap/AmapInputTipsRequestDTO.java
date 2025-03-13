package cc.xiaoxu.cloud.bean.dto.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德地图输入提示 - 请求参数")
public class AmapInputTipsRequestDTO {

    @Schema(description = "搜索关键词", required = true)
    private String keywords;

    @Schema(description = "搜索区域，可选值：城市中文、中文全拼、citycode、adcode")
    private String city;

    @Schema(description = "坐标点，格式：经度,纬度")
    private String location;

    @Schema(description = "是否强制限制在设置的城市内搜索，可选值：true/false")
    private Boolean cityLimit;
}
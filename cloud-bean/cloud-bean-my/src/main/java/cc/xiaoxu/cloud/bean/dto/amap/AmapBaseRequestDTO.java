package cc.xiaoxu.cloud.bean.dto.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德地图 - 地址搜索 - 基础请求参数")
public class AmapBaseRequestDTO {

    @Schema(description = "搜索关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keywords;

    @Schema(description = "搜索区域，可选值：城市中文、中文全拼、citycode、adcode、矩形区域、圆形区域、多边形区域")
    private String region;

    @Schema(description = "是否强制限制在设置的城市内搜索，可选值：true/false")
    private Boolean cityLimit;
}
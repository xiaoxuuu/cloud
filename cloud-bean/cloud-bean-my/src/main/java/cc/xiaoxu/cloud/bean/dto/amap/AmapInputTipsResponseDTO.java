package cc.xiaoxu.cloud.bean.dto.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德地图输入提示 - 响应结果")
public class AmapInputTipsResponseDTO {

    @Schema(description = "返回结果状态值，值为0或1，0表示请求失败；1表示请求成功")
    private String status;

    @Schema(description = "返回状态说明")
    private String info;

    @Schema(description = "状态码")
    private String infocode;

    @Schema(description = "建议结果数目")
    private String count;

    @Schema(description = "输入提示列表")
    private List<AmapInputTipDTO> tips;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "输入提示项")
    public static class AmapInputTipDTO {

        @Schema(description = "名称")
        private String name;

        @Schema(description = "所属区域")
        private String district;

        @Schema(description = "地址")
        private String address;

        @Schema(description = "坐标点，格式：经度,纬度")
        private String location;

        @Schema(description = "POI的ID")
        private String id;

        @Schema(description = "输入提示的类型")
        private String typecode;

        @Schema(description = "城市编码")
        private String adcode;
    }
}
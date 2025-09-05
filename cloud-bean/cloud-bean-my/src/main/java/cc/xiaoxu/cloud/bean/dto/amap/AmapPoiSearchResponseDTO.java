package cc.xiaoxu.cloud.bean.dto.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德地图搜索POI 2.0 - 响应结果")
public class AmapPoiSearchResponseDTO {

    @Schema(description = "返回结果状态值，值为0或1，0表示请求失败；1表示请求成功")
    private String status;

    @Schema(description = "返回状态说明")
    private String info;

    @Schema(description = "状态码")
    private String infocode;

    @Schema(description = "搜索结果总数")
    private String count;

    @Schema(description = "POI信息列表")
    private List<AmapPoiDTO> pois;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "POI信息")
    public static class AmapPoiDTO {

        @Schema(description = "POI名称")
        private String name;

        @Schema(description = "POI的ID")
        private String id;

        @Schema(description = "坐标点，格式：经度,纬度")
        private String location;

        @Schema(description = "POI类型")
        private String type;

        @Schema(description = "POI类型编码")
        private String typecode;

        @Schema(description = "POI所在省份名称")
        private String pname;

        @Schema(description = "POI所在城市名称")
        private String cityname;

        @Schema(description = "POI所在区县名称")
        private String adname;

        @Schema(description = "地址")
        private String address;

        @Schema(description = "邮编")
        private String postcode;

        @Schema(description = "POI所在区县编码")
        private String adcode;

        @Schema(description = "poi 所属城市编码")
        private String citycode;

        @Schema(description = "电话")
        private String tel;

        @Schema(description = "距离中心点的距离，单位：米")
        private String distance;

        @Schema(description = "网站")
        private String website;

        @Schema(description = "邮箱")
        private String email;

        @Schema(description = "商户信息")
        private AmapBusinessDTO business;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "商户信息")
    public static class AmapBusinessDTO {

        @Schema(description = "营业时间")
        private String opentime;

        @Schema(description = "人均消费")
        private String cost;

        @Schema(description = "评分")
        private String rating;

        @Schema(description = "评价数")
        private String reviewCount;

        @Schema(description = "商户标签")
        private String tag;
    }
}
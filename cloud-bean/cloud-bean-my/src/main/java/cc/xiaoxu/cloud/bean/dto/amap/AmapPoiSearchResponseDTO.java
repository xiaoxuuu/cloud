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

        @Schema(description = "poi 所属省份编码")
        private String pcode;

        @Schema(description = "POI所在区县编码")
        private String adcode;

        @Schema(description = "poi 所属城市编码")
        private String citycode;

        @Schema(description = "商户信息")
        private AmapBusinessDTO business;

        @Schema(description = "室内相关信息")
        private AmapIndoorDTO indoor;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "商户信息")
    public static class AmapBusinessDTO {

        @Schema(description = "poi 所属商圈")
        private String business_area;

        @Schema(description = "poi 今日营业时间，如 08:30-17:30 08:30-09:00 12:00-13:30 09:00-13:00")
        private String opentime_today;

        @Schema(description = "poi 营业时间描述，如 周一至周五:08:30-17:30(延时服务时间:08:30-09:00；12:00-13:30)；周六延时服务时间:09:00-13:00(法定节假日除外)")
        private String opentime_week;

        @Schema(description = "poi 的联系电话")
        private String tel;

        @Schema(description = "poi 特色内容，目前仅在美食poi下返回")
        private String tag;

        @Schema(description = "poi 评分，目前仅在餐饮、酒店、景点、影院类 POI 下返回")
        private String rating;

        @Schema(description = "poi 人均消费，目前仅在餐饮、酒店、景点、影院类 POI 下返回")
        private String cost;

        @Schema(description = "停车场类型（地下、地面、路边），目前仅在停车场类 POI 下返回")
        private String parking_type;

        @Schema(description = "poi 的别名，无别名时不返回")
        private String alias;

        @Schema(description = "poi 标识，用于确认poi信息类型")
        private String keytag;

        @Schema(description = "用于再次确认信息类型")
        private String rectag;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "室内相关信息")
    public static class AmapIndoorDTO {

        @Schema(description = "是否有室内地图标志，1为有，0为没有")
        private String indoor_map;

        @Schema(description = "如果当前 POI 为建筑物类 POI，则 cpid 为自身 POI ID；如果当前 POI 为商铺类 POI，则 cpid 为其所在建筑物的 POI ID。indoor_map 为0时不返回")
        private String cpid;

        @Schema(description = "楼层索引，一般会用数字表示，例如8；indoor_map 为0时不返回")
        private String floor;

        @Schema(description = "所在楼层，一般会带有字母，例如F8；indoor_map 为0时不返回")
        private String truefloor;
    }
}
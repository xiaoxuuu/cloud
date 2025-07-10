package cc.xiaoxu.cloud.bean.dto;

import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 新增 - 请求参数")
public class PointAddDTO {

    @Schema(description = "点位类型")
    private PointTypeEnum pointType;

    @Schema(description = "简称")
    private String pointShortName;

    @Schema(description = "全称")
    private String pointFullName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "收藏次数")
    private Integer collectTimes;

    @Schema(description = "我去过的次数")
    private Integer visitedTimes;

    @Schema(description = "来源")
    private List<PointSourceAddDTO> source;

    @Schema(description = "地址code")
    private String addressCode;
}
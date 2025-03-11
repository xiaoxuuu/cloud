package cc.xiaoxu.cloud.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 全参数 - 响应参数")
public class PointFullVO extends PointVO {

    @Schema(description = "地址code")
    private String addressCode;

    @Schema(description = "高德 - WIA 坐标（来源高德小程序）")
    private String amapWia;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "高德 - 更新时间")
    private Date amapUpdateTime;

    @Schema(description = "高德 - 标签")
    private String amapTag;

    @Schema(description = "高德 - 评分")
    private String amapRating;

    @Schema(description = "高德 - 人均消费")
    private String amapCost;

    @Schema(description = "高德 - POI ID")
    private String amapPoiId;
}
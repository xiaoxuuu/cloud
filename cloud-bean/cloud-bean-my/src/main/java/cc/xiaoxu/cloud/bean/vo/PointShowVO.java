package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 业务参数 - 响应参数")
public class PointShowVO extends PointSimpleVO {

    @Schema(description = "全称")
    private String pointFullName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "推荐距离(米）")
    private Integer recommendedDistance;

    @Schema(description = "营业时间")
    private String openingHours;

    @Schema(description = "联系电话")
    private String telephone;

    @Schema(description = "人均（分）")
    private Integer cost;

    @Schema(description = "更新时间")
    private Date modifyTime;

    @Schema(description = "标签")
    private List<PointTagShowVO> tagList;

    @Schema(description = "来源")
    private List<PointSourceShowVO> sourceList;

    @Schema(description = "电话")
    private List<String> telList;
}
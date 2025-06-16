package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 地图搜索 - 营业信息 - 响应参数")
public class PointMapSearchBusinessVO {

    @Schema(description = "电话")
    private String tel;

    @Schema(description = "网站")
    private String website;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "营业时间")
    private String openTime;

    @Schema(description = "人均消费")
    private String averageCost;

    @Schema(description = "评分")
    private String rating;

    @Schema(description = "商户标签")
    private String tag;
}
package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 全参数 - 响应参数")
public class PointFullVO extends PointVO {

    @Schema(description = "来源")
    private List<PointSourceVO> pointSourceList;

    @Schema(description = "标签")
    private List<String> tagList;

    @Schema(description = "电话")
    private List<String> telList;

    @Schema(description = "营业时间")
    private String openTime;
}
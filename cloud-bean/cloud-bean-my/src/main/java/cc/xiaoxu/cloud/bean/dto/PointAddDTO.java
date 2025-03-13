package cc.xiaoxu.cloud.bean.dto;

import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位 - 新增 - 请求参数")
public class PointAddDTO {

    @Schema(description = "点位类型")
    @TableField(value = "point_type")
    private PointTypeEnum pointType;

    @Schema(description = "名称")
    private String pointName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "来源")
    private String source;
}
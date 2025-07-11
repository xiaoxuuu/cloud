package cc.xiaoxu.cloud.bean.dto;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位来源 - 编辑 - 请求参数")
public class PointSourceEditDTO extends PointSourceAddDTO {

    @Schema(description = "数据id")
    private String id;

    @Schema(description = "状态")
    private StateEnum state;
}
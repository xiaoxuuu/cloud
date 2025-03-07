package cc.xiaoxu.cloud.bean.vo;

import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位类型 - 响应参数")
public class PointTypeVO {

    @Schema(description = "code")
    private String code;

    @Schema(description = "描述")
    private String desc;

    public PointTypeVO(PointTypeEnum pointTypeEnum) {
        this.code = pointTypeEnum.getCode();
        this.desc = pointTypeEnum.getIntroduction();
    }
}
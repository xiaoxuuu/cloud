package cc.xiaoxu.cloud.core.bean.entity;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.date.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity extends BaseInfoEntity {

    @Schema(description = "创建人id")
    private Integer createId;

    @Schema(description = "编辑人id")
    private Integer modifyId;

    public static void buildCreate(BaseEntity entity, Integer userId) {

        entity.setState(StateEnum.ENABLE.getCode());
        entity.setCreateTime(DateUtils.toDate(LocalDateTime.now()));
        entity.setCreateId(userId);
    }
}
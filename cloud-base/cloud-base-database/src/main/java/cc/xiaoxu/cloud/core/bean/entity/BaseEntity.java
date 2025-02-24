package cc.xiaoxu.cloud.core.bean.entity;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键id")
    private Integer id;

    /**
     * {@link StateEnum StateEnum}
     */
    @Schema(description = "状态")
    private String state;

    @Schema(description = "描述")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "创建人id")
    private Integer createId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "编辑时间")
    private Date modifyTime;

    @Schema(description = "编辑人id")
    private Integer modifyId;

    public static void buildCreate(BaseEntity entity, Integer userId) {

        entity.setState(StateEnum.ENABLE.getCode());
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateId(userId);
    }
}
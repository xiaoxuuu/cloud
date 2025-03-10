package cc.xiaoxu.cloud.core.bean.entity;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
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
public class BaseInfoEntity extends BaseIdEntity {

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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "编辑时间")
    private Date modifyTime;
}
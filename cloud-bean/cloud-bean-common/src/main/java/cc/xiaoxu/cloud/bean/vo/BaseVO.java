package cc.xiaoxu.cloud.bean.vo;

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
public class BaseVO extends BaseIdVO {

    @Schema(description = "主键id")
    private Integer id;

    /**
     * {@link StateEnum StateEnum}
     */
    @Schema(description = "状态")
    private String state;

    /**
     * 描述
     */
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
}
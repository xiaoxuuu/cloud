package cc.xiaoxu.cloud.core.bean.entity;

import cc.xiaoxu.cloud.core.bean.enums.StateEnum;
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
    private String id;

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
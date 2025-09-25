package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.bean.entity.BaseIdEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_area")
@NoArgsConstructor
@AllArgsConstructor
public class Area extends BaseIdEntity {

    @Schema(description = "层级：1 省 2 市 3 区 4 街道 5 居委会")
    private String level;

    @Schema(description = "本层级 code")
    private String code;

    @Schema(description = "短code")
    private String shortCode;

    @Schema(description = "本层级名称")
    private String name;

    @Schema(description = "办公地点所在经纬度")
    private String location;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    /**
     * {@link StateEnum StateEnum}
     */
    @Schema(description = "状态")
    private String state;
}
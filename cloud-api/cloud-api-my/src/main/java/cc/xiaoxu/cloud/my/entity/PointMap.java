package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.bean.enums.MapTypeEnum;
import cc.xiaoxu.cloud.core.bean.entity.BaseInfoEntity;
import cc.xiaoxu.cloud.core.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_point_map")
@NoArgsConstructor
@AllArgsConstructor
public class PointMap extends BaseInfoEntity {

    @Schema(description = "地点类型")
    private MapTypeEnum mapType;

    @Schema(description = "地点id")
    private String mapId;

    @Schema(description = "详细数据")
    @TableField(value = "map_result", typeHandler = JsonbTypeHandler.class)
    private Object mapResult;
}
package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseInfoEntity;
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

    @Schema(description = "地点id")
    private String pointId;

    @Schema(description = "高德地图 - WIA 坐标（来源高德小程序）")
    private String amapWia;

    @Schema(description = "高德结果")
    private String amapResult;
}
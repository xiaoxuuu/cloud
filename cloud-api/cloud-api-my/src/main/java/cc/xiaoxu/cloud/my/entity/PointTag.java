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
@TableName("t_point_tag")
@NoArgsConstructor
@AllArgsConstructor
public class PointTag extends BaseInfoEntity {

    @Schema(description = "标签名")
    private String tagName;

    @Schema(description = "颜色")
    private String color;
}
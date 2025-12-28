package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.bean.enums.PointSourceTypeEnum;
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
@TableName("t_point_source")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏点位来源 - 实体")
public class PointSource extends BaseInfoEntity {

    @Schema(description = "作者id")
    private Integer authorId;

    @Schema(description = "类型")
    private PointSourceTypeEnum type;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "链接")
    private String url;
}
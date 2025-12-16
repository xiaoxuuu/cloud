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
@TableName("t_point_source_author")
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点位来源作者 - 实体")
public class PointSourceAuthor extends BaseInfoEntity {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "抖音链接")
    private String tiktokUrl;

    @Schema(description = "小红书链接")
    private String redbookUrl;

    @Schema(description = "B站链接")
    private String bilibiliUrl;

    @Schema(description = "内容")
    private String content;
}
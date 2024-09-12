package cc.xiaoxu.cloud.ai.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntityForPostgres;
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
@TableName("t_knowledge")
@NoArgsConstructor
@AllArgsConstructor
public class Knowledge extends BaseEntityForPostgres {

    @Schema(description = "资源类型：文件、数据表、自定义分类")
    private String type;

    @Schema(description = "租户")
    private String tenant;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "三方平台文件 id")
    private String threePartyFileId;

    @Schema(description = "三方平台附加信息")
    private String threePartyInfo;

    @Schema(description = "资源处理状态")
    private String status;
}
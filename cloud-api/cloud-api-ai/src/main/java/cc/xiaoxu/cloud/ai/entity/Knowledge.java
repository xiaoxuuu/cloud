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

    @Schema(description = "租户")
    private String userId;

    @Schema(description = "资源类型：文件、数据表等")
    private String type;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "文件 id")
    private String fileId;

    @Schema(description = "文件信息")
    private String fileInfo;

    @Schema(description = "资源处理状态")
    private String status;
}
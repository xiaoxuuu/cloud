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
@TableName("t_conversation")
@NoArgsConstructor
@AllArgsConstructor
public class Conversation extends BaseEntityForPostgres {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "模型id")
    private Integer modelId;
}
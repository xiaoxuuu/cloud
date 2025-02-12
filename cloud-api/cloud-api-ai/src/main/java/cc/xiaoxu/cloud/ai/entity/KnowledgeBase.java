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
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_knowledge_base")
@Schema(description = "知识库")
public class KnowledgeBase extends BaseEntityForPostgres {

    @Schema(description = "用户")
    private Integer userId;

    @Schema(description = "知识库名")
    private String name;
}
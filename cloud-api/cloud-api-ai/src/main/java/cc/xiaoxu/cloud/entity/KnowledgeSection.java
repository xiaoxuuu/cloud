package cc.xiaoxu.cloud.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntityForPostgre;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("t_knowledge_section")
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSection extends BaseEntityForPostgre {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键id")
    private Integer id;

    @Schema(description = "知识id")
    private Integer knowledgeId;

    @Schema(description = "知识切片内容")
    private String cutContent;

    @Schema(description = "知识切片向量，1536 维")
    private String embedding;
}
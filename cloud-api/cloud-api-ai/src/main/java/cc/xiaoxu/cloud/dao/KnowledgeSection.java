package cc.xiaoxu.cloud.dao;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_knowledge_section")
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeSection extends BaseEntity {

    @Schema(description = "知识id")
    private String knowledgeId;

    @Schema(description = "知识切片内容")
    private String cutContent;

    @Schema(description = "知识切片向量，1536 维")
    private List<Double> embedding;
}
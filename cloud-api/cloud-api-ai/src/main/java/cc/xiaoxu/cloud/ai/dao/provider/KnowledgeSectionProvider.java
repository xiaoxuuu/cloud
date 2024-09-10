package cc.xiaoxu.cloud.ai.dao.provider;

import cc.xiaoxu.cloud.ai.entity.KnowledgeSection;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.dao.BaseProvider;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

public class KnowledgeSectionProvider extends BaseProvider<KnowledgeSection> {

    private static final KnowledgeSectionProvider PROVIDER = new KnowledgeSectionProvider();

    public static KnowledgeSectionProvider get() {
        return PROVIDER;
    }

    public String getSimilarityData(@Param("embedding") String embedding, @Param("askDTO") AskDTO askDTO) {

        String embeddingStr = "'" + embedding + "'";

        SQL sql = new SQL() {{
            SELECT(" * ");
            SELECT(" embedding <=> " + embeddingStr + " AS distance ");
            FROM("t_knowledge_section");
            if (StringUtils.isNotBlank(askDTO.getKnowledgeId())) {
                WHERE("knowledge_id IN (" + String.join(",", askDTO.getKnowledgeId().split(",")) + " )");
            }
            WHERE("embedding <=> " + embeddingStr + " < " + askDTO.getSimilarity());
            WHERE("state = " + StateEnum.ENABLE.getCode() + "'");
            ORDER_BY("embedding <=> " + embeddingStr + " ASC ");
            LIMIT(askDTO.getSimilarityContentNum());
        }};
        return sql.toString();
    }
}
package cc.xiaoxu.cloud.dao.provider;

import cc.xiaoxu.cloud.core.dao.BaseProvider;
import cc.xiaoxu.cloud.entity.KnowledgeSection;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

/**
 * 菜单provider
 * </p>
 *
 * @author Matt
 */
public class KnowledgeSectionProvider extends BaseProvider<KnowledgeSection> {

    private static final KnowledgeSectionProvider PROVIDER = new KnowledgeSectionProvider();

    public static KnowledgeSectionProvider get() {
        return PROVIDER;
    }

    public String getSimilarityData(@Param("embedding") String embedding, @Param("similarity") Double similarity, @Param("similarityContentNum") Integer similarityContentNum) {

        String embeddingStr = "'" + embedding + "'";

        SQL sql = new SQL() {{
            SELECT(" * ");
            SELECT(" embedding <=> " + embeddingStr + " AS distance ");
            FROM("t_knowledge_section");
            WHERE("embedding <=> " + embeddingStr + " < " + similarity);
            ORDER_BY("embedding <=> " + embeddingStr + " DESC ");
            LIMIT(similarityContentNum);
        }};
        return sql.toString();
    }
}
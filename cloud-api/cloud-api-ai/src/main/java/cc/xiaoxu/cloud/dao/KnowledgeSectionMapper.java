package cc.xiaoxu.cloud.dao;

import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.dao.provider.KnowledgeSectionProvider;
import cc.xiaoxu.cloud.entity.KnowledgeSection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface KnowledgeSectionMapper extends BaseMapper<KnowledgeSection> {

    @Update("UPDATE t_knowledge_section SET embedding = '${embedding}' WHERE id = #{id}")
    void updateEmbedding(@Param("embedding") String embedding, @Param("id") Integer id);

    @SelectProvider(type = KnowledgeSectionProvider.class, method = "getSimilarityData")
    List<KnowledgeSectionVO> getSimilarityData(@Param("embedding") String embedding, @Param("similarity") Double similarity, @Param("similarityContentNum") Integer similarityContentNum);
}
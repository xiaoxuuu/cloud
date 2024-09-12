package cc.xiaoxu.cloud.ai.dao;

import cc.xiaoxu.cloud.ai.dao.provider.KnowledgeSectionProvider;
import cc.xiaoxu.cloud.ai.entity.KnowledgeSection;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
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
    List<KnowledgeSectionExpandVO> getSimilarityData(@Param("embedding") String embedding, @Param("askDTO") AskDTO askDTO, @Param("tenant") String tenant);
}
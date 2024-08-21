package cc.xiaoxu.cloud.dao;

import cc.xiaoxu.cloud.entity.KnowledgeSection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface KnowledgeSectionMapper extends BaseMapper<KnowledgeSection> {

    @Update("UPDATE t_knowledge_section SET embedding = '${embedding}' WHERE id = #{id}")
    void updateEmbedding(@Param("embedding") String embedding, @Param("id") Integer id);
}
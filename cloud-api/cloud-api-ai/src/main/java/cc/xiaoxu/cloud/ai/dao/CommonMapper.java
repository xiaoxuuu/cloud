package cc.xiaoxu.cloud.ai.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommonMapper {

    @Select("${sql}")
    List<String> sql(@Param("sql") String sql);
}
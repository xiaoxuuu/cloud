package cc.xiaoxu.cloud.my.dao;

import cc.xiaoxu.cloud.my.entity.Area;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AreaMapper extends BaseMapper<Area> {

    @Select("""
            SELECT
                "level",
                code,
                "name"
            FROM
                t_area
            WHERE
                "level" IN (1, 2) 
              AND code NOT LIKE '8%'
            ORDER BY
                code
            """)
    List<Area> tree();
}
package cc.xiaoxu.cloud.my.dao;

import cc.xiaoxu.cloud.my.entity.Point;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PointMapper extends BaseMapper<Point> {

    @Select("""
            SELECT COUNT(1) FROM t_point WHERE "state" = 'P'
            """)
    Integer countProgressing();
}
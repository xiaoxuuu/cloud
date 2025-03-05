package cc.xiaoxu.cloud.my.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface FoodMapper {

    @Select("""
            SELECT * FROM t_food WHERE "del" = false
            """)
    List<Map> getFood();
}
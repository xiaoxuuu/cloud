package cc.xiaoxu.cloud.my.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommonMapper {

    @Select("SELECT * FROM t_food WHERE")
    List getFood();
}
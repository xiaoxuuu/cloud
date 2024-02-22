package cc.xiaoxu.cloud.my.navigation.dao;

import cc.xiaoxu.cloud.my.navigation.bean.entity.NavWebsite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NavWebsiteMapper extends BaseMapper<NavWebsite> {

    @Select("UPDATE t_nav_website SET visit_num = visit_num + 1 WHERE id = #{id}")
    void update(@Param("id") String id);
}
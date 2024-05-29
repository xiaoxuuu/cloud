package cc.xiaoxu.cloud.my.nav.dao.mysql;

import cc.xiaoxu.cloud.my.nav.bean.mysql.NavWebsite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface NavWebsiteMapper extends BaseMapper<NavWebsite> {

    @Select("UPDATE t_nav_website SET visit_num = visit_num + 1 WHERE id = #{id}")
    void updateNum(@Param("id") String id);
}
package cc.xiaoxu.cloud.my.dao;

import cc.xiaoxu.cloud.bean.dto.NavWebsitePageDTO;
import cc.xiaoxu.cloud.my.entity.NavWebsite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

@Mapper
public interface NavWebsiteMapper extends BaseMapper<NavWebsite> {

    @Select("UPDATE t_nav_website SET visit_num = visit_num + 1 WHERE id = #{id}")
    void updateNum(@Param("id") Integer id);

    @SelectProvider(type = NavWebsiteProvider.class, method = "pages")
    Page<NavWebsite> pages(@Param("dto") NavWebsitePageDTO dto, @Param("page") Page<NavWebsite> page);
}
package cc.xiaoxu.cloud.my.dao;

import cc.xiaoxu.cloud.bean.dto.NavWebsitePageDTO;
import cc.xiaoxu.cloud.core.dao.BaseProvider;
import cc.xiaoxu.cloud.my.entity.NavWebsite;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.jdbc.SQL;

import java.util.List;
import java.util.Set;

public class NavWebsiteProvider extends BaseProvider<NavWebsite> {

    private static final NavWebsiteProvider PROVIDER = new NavWebsiteProvider();

    public static NavWebsiteProvider get() {
        return PROVIDER;
    }

    public String pages(@Param("dto") NavWebsitePageDTO dto, @Param("page") Page<NavWebsite> page) {

        SQL sql = new SQL();
        select(sql);
        from(sql);

        test(NavWebsite::getWebsiteName);

        // 关键字
        sql.OR().WHERE(getTablePrefix() + "." + "short_name" + " LIKE '%" + dto.getKeyword() + "%'");
        sql.OR().WHERE(getTablePrefix() + "." + "website_name" + " LIKE '%" + dto.getKeyword() + "%'");
        sql.OR().WHERE(getTablePrefix() + "." + "url" + " LIKE '%" + dto.getKeyword() + "%'");
        sql.OR().WHERE(getTablePrefix() + "." + "description" + " LIKE '%" + dto.getKeyword() + "%'");
        // 时间
        moreThan(StringUtils.isNotEmpty(dto.getLastAvailableTimeStart()), "last_available_time", dto.getLastAvailableTimeStart(), getTablePrefix(), sql);
        lessThan(StringUtils.isNotEmpty(dto.getLastAvailableTimeEnd()), "last_available_time", dto.getLastAvailableTimeEnd(), getTablePrefix(), sql);
        // 类型
        like(StringUtils.isNotEmpty(dto.getType()), "type", dto.getType(), sql);
        // 访问次数
        eq(StringUtils.isNotEmpty(dto.getVisitNum()), "visit_num", dto.getVisitNum(), sql);
        // 标签
        in(CollectionUtils.isNotEmpty(dto.getLabelList()), "label", dto.getLabelList(), sql);

        // 图标是否存在
        if (null != dto.getHaveIcon()) {
            join(sql, NavWebsiteIconProvider.get(), Set.of(), "icon_id", "id");
            isNotNull(NavWebsiteIconProvider.get().getTablePrefix(), "status", sql);
        }

        // 排序
        sort(dto.getOrders(), List.of(this), sql);
        return sql.toString();
    }
}
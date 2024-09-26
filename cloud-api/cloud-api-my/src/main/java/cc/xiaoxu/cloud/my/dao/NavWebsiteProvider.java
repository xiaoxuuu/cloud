package cc.xiaoxu.cloud.my.dao;

import cc.xiaoxu.cloud.bean.dto.NavWebsitePageDTO;
import cc.xiaoxu.cloud.core.dao.BaseProvider;
import cc.xiaoxu.cloud.my.entity.NavWebsite;
import cc.xiaoxu.cloud.my.entity.NavWebsiteIcon;
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

        // 关键字
        or(sql, getTablePrefix() + "." + "short_name" + " LIKE '%" + dto.getKeyword() + "%'",
                getTablePrefix() + "." + "website_name" + " LIKE '%" + dto.getKeyword() + "%'",
                getTablePrefix() + "." + "url" + " LIKE '%" + dto.getKeyword() + "%'",
                getTablePrefix() + "." + "description" + " LIKE '%" + dto.getKeyword() + "%'");
        // 时间
        moreThan(StringUtils.isNotEmpty(dto.getLastAvailableTimeStart()), NavWebsite::getLastAvailableTime, dto.getLastAvailableTimeStart(), getTablePrefix(), sql);
        lessThan(StringUtils.isNotEmpty(dto.getLastAvailableTimeEnd()), NavWebsite::getLastAvailableTime, dto.getLastAvailableTimeEnd(), getTablePrefix(), sql);
        // 类型
        like(StringUtils.isNotEmpty(dto.getType()), NavWebsite::getType, dto.getType(), sql);
        // 访问次数
        eq(StringUtils.isNotEmpty(dto.getVisitNum()), NavWebsite::getVisitNum, dto.getVisitNum(), sql);
        // 标签
        in(CollectionUtils.isNotEmpty(dto.getLabelList()), NavWebsite::getLabel, dto.getLabelList(), sql);

        // 图标是否存在
        if (null != dto.getHaveIcon()) {
            join(sql, NavWebsiteIconProvider.get(), Set.of(), "icon_id", "id");
            NavWebsiteIconProvider.get().isNotNull(NavWebsiteIconProvider.get().getTablePrefix(), NavWebsiteIcon::getId, sql);
        }

        // 排序
        sort(dto.getOrders(), List.of(this), sql);
        return sql.toString();
    }
}
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
        or(sql,
                like(null, NavWebsite::getShortName, dto.getKeyword()),
                like(null, NavWebsite::getWebsiteName, dto.getKeyword()),
                like(null, NavWebsite::getUrl, dto.getKeyword()),
                like(null, NavWebsite::getDescription, dto.getKeyword()));
        // 时间
        moreThan(sql, StringUtils.isNotEmpty(dto.getLastAvailableTimeStart()), NavWebsite::getLastAvailableTime, dto.getLastAvailableTimeStart(), getTablePrefix());
        lessThan(sql, StringUtils.isNotEmpty(dto.getLastAvailableTimeEnd()), NavWebsite::getLastAvailableTime, dto.getLastAvailableTimeEnd(), getTablePrefix());
        // 类型
        like(sql, StringUtils.isNotEmpty(dto.getType()), NavWebsite::getType, dto.getType());
        // 访问次数
        eq(sql, StringUtils.isNotEmpty(dto.getVisitNum()), NavWebsite::getVisitNum, dto.getVisitNum());
        // 标签
        in(sql, CollectionUtils.isNotEmpty(dto.getLabelList()), NavWebsite::getLabel, dto.getLabelList());

        // 图标是否存在
        if (null != dto.getHaveIcon()) {
            join(sql, NavWebsiteIconProvider.get(), Set.of(), "icon_id", "id");
            if (dto.getHaveIcon()) {
                NavWebsiteIconProvider.get().isNotNull(sql, NavWebsiteIconProvider.get().getTablePrefix(), NavWebsiteIcon::getId);
            } else {
                NavWebsiteIconProvider.get().isNull(sql, NavWebsiteIconProvider.get().getTablePrefix(), NavWebsiteIcon::getId);
            }
        }

        // 排序
        sort(sql, dto.getOrders(), List.of(this));
        return sql.toString();
    }
}
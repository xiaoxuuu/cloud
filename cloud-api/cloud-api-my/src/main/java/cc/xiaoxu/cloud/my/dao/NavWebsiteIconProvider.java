package cc.xiaoxu.cloud.my.dao;

import cc.xiaoxu.cloud.core.dao.BaseProvider;
import cc.xiaoxu.cloud.my.entity.NavWebsiteIcon;

public class NavWebsiteIconProvider extends BaseProvider<NavWebsiteIcon> {

    private static final NavWebsiteIconProvider PROVIDER = new NavWebsiteIconProvider();

    public static NavWebsiteIconProvider get() {
        return PROVIDER;
    }
}
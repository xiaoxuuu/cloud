package cc.xiaoxu.cloud.core.dao;

import live.xiaoxu.cloud.plugin.mysql.bean.entity.BaseEntity;

public class BaseEntityProvider extends BaseProvider<BaseEntity> {

    private static final BaseEntityProvider provider = new BaseEntityProvider();

    public static BaseEntityProvider get() {
        return provider;
    }
}
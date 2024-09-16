package cc.xiaoxu.cloud.core.dao;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import cc.xiaoxu.cloud.core.utils.ProviderUtils;
import cc.xiaoxu.cloud.core.utils.text.ChartUtils;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.util.*;

public class BaseProvider<T> {

    private static final String TABLE_PREFIX = "suffix_";

    private final Class<?>[] typeArguments = GenericTypeUtils.resolveTypeArguments(getClass(), BaseProvider.class);

    private static final BaseProvider<?> provider = new BaseProvider<>();

    public static BaseProvider<?> get() {
        return provider;
    }

    @SuppressWarnings("unchecked")
    public Class<T> currentModelClass() {
        return (Class<T>) this.typeArguments[0];
    }

    public String getTableName() {

        TableName annotation = currentModelClass().getAnnotation(TableName.class);
        return annotation.value();
    }

    public String getTablePrefix() {

        return TABLE_PREFIX + getTableName();
    }

    public Map<String, String> getFieldMap() {

        Class<?> tClass = currentModelClass();
        List<Field> allField = getAllField(tClass);
        Map<String, String> fieldMap = new HashMap<>(allField.size());
        for (Field field : allField) {
            field.setAccessible(true);
            String name = field.getName();
            String columnName;
            TableField tableField = field.getAnnotation(TableField.class);
            if (Objects.nonNull(tableField)) {
                columnName = tableField.value();
            } else {
                columnName = ChartUtils.camelToUnderline(name, 1);
            }
            fieldMap.put(name, columnName.trim().replace("`", ""));
        }
        return fieldMap;
    }

    public List<String> getColumnList() {

        Class<?> tClass = currentModelClass();
        List<Field> allField = getAllField(tClass);
        List<String> fieldList = new LinkedList<>();
        for (Field field : allField) {
            field.setAccessible(true);
            String columnName;
            TableField tableField = field.getAnnotation(TableField.class);
            if (Objects.nonNull(tableField)) {
                columnName = tableField.value();
            } else {
                columnName = ChartUtils.camelToUnderline(field.getName(), 1);
            }
            fieldList.add(columnName);
        }
        return fieldList;
    }

    private static List<Field> getAllField(Class<?> tClass) {

        List<Field> fieldList = new LinkedList<>();
        if (tClass == Object.class) {
            return fieldList;
        }
        Field[] fields = tClass.getDeclaredFields();
        fieldList.addAll(Arrays.asList(fields));
        Class<?> superclass = tClass.getSuperclass();
        fieldList.addAll(getAllField(superclass));
        return fieldList;
    }

    /**
     * select
     *
     * @param sql          需要附加的 sql
     */
    public void select(SQL sql) {

        select(sql, Set.of());
    }

    public void select(SQL sql, Set<String> excludeFieldSet) {

        select(sql, excludeFieldSet, this);
    }

    /**
     * join 附加 select
     *
     * @param sql             需要附加的 sql
     * @param baseProvider    对应表
     * @param excludeFieldSet 排除字段
     */
    public static <M> void select(SQL sql, Set<String> excludeFieldSet, BaseProvider<M> baseProvider) {

        Map<String, String> fieldMap = baseProvider.getFieldMap();
        for (Map.Entry<String, String> entry : fieldMap.entrySet()) {
            if (excludeFieldSet.contains(entry.getValue())) {
                continue;
            }
            sql.SELECT(baseProvider.getTablePrefix() + ".`" + entry.getValue() + "` AS `" + entry.getKey() + "`");
        }
    }

    public void from(SQL sql) {

        sql.FROM(getTableName() + " " + getTablePrefix());
    }

    /**
     * @param sql                   需要附加的 sql
     * @param baseProvider          需要连接的目标表
     * @param sourceColumnUnderline 源表字段
     * @param targetColumnUnderline 目标表字段
     * @param <M>                   目标类
     */
    public <M> void join(SQL sql, BaseProvider<M> baseProvider, Set<String> excludeFieldSet, String sourceColumnUnderline, String targetColumnUnderline) {

        select(sql, excludeFieldSet, baseProvider);
        sql.LEFT_OUTER_JOIN(baseProvider.getTableName() + " " + baseProvider.getTablePrefix() + " ON " + baseProvider.getTablePrefix() + "." + targetColumnUnderline + " = " + getTablePrefix() + "." + sourceColumnUnderline);
    }

    public void where(SQL sql, String condition) {

        sql.WHERE(getTablePrefix() + "." + condition);
    }

    public void sort(List<OrderItem> orderList, List<BaseProvider<? extends BaseEntity>> baseProviderList, SQL sql) {
        Map<String, BaseProvider<? extends BaseEntity>> map = new HashMap<>();
        for (int i = baseProviderList.size() - 1; i >= 0; i--) {
            BaseProvider<? extends BaseEntity> baseProvider = baseProviderList.get(i);
            for (String s : baseProvider.getFieldMap().keySet()) {
                map.put(s, baseProvider);
            }
        }
        if (CollectionUtils.isEmpty(orderList)) {
            orderList = ProviderUtils.getDefaultSort();
        }
        for (OrderItem orderItem : orderList) {
            String column = orderItem.getColumn();
            if (!map.containsKey(column)) {
                continue;
            }
            sql.ORDER_BY(column + ProviderUtils.asc(orderItem.isAsc()));
        }
    }
}
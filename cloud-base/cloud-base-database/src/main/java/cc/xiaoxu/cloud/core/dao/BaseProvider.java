package cc.xiaoxu.cloud.core.dao;

import cc.xiaoxu.cloud.bean.dto.OrderItemDTO;
import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import cc.xiaoxu.cloud.core.handler.SqlConcatFactory;
import cc.xiaoxu.cloud.core.handler.SqlConcatHandler;
import cc.xiaoxu.cloud.core.utils.ConditionUtils;
import cc.xiaoxu.cloud.core.utils.ProviderUtils;
import cc.xiaoxu.cloud.core.utils.text.ChartUtils;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.LambdaMeta;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class BaseProvider<T> {

    private static final String TABLE_PREFIX = "prefix_";

    private final Class<?>[] typeArguments = GenericTypeUtils.resolveTypeArguments(getClass(), BaseProvider.class);

    private static final BaseProvider<?> provider = new BaseProvider<>();

    private static final SqlConcatHandler<?> concat = SqlConcatFactory.get();

    public static BaseProvider<?> get() {
        return provider;
    }

    public BaseProvider<?> getInstance() {
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
            concat.select(sql, baseProvider, entry.getValue(), entry.getKey());
//            sql.SELECT(baseProvider.getTablePrefix() + ".`" + entry.getValue() + "` AS `" + entry.getKey() + "`");
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

    public void sort(List<OrderItemDTO> orderList, List<BaseProvider<? extends BaseEntity>> baseProviderList, SQL sql) {
        Map<String, BaseProvider<? extends BaseEntity>> map = new HashMap<>();
        for (int i = baseProviderList.size() - 1; i >= 0; i--) {
            BaseProvider<? extends BaseEntity> baseProvider = baseProviderList.get(i);
            for (String s : baseProvider.getFieldMap().keySet()) {
                map.put(s, baseProvider);
            }
        }
        if (CollectionUtils.isEmpty(orderList)) {
            orderList = OrderItemDTO.getDefaultSort();
        }
        for (OrderItemDTO orderItem : orderList) {
            String column = orderItem.getColumn();
            if (!map.containsKey(column)) {
                continue;
            }
            sql.ORDER_BY(column + ProviderUtils.asc(orderItem.isAsc()));
        }
    }

    public String notLike(SFunction<T, ?> column, String data, SQL sql) {
        return notLike(true, column, data, sql);
    }

    public String notLike(boolean use, SFunction<T, ?> column, String data, SQL sql) {
        if (!use) {
            return "";
        }
        String conditions = getTablePrefix() + "." + getColumn(column) + " NOT LIKE '%" + data + "%'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String like(SFunction<T, ?> column, String data, SQL sql) {
        return like(true, column, data, sql);
    }

    public String like(boolean use, SFunction<T, ?> column, String data, SQL sql) {
        if (!use) {
            return "";
        }
        String conditions = getTablePrefix() + "." + getColumn(column) + " LIKE '%" + data + "%'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String isNotNull(String table, SFunction<T, ?> column, SQL sql) {
        String conditions = table + "." + getColumn(column) + " IS NOT NULL ";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String isNull(String table, SFunction<T, ?> column, SQL sql) {
        String conditions = table + "." + getColumn(column) + " IS NULL ";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String ne(SFunction<T, ?> column, String data, SQL sql) {
        return ne(true, column, data, sql);
    }

    public String ne(boolean use, SFunction<T, ?> column, String data, SQL sql) {
        if (!use) {
            return "";
        }
        String conditions = getTablePrefix() + "." + getColumn(column) + " != '" + data + "'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public void eq(boolean use, SFunction<T, ?> column, String data, SQL sql) {
        if (!use) {
            return;
        }
        sql.WHERE(getTablePrefix() + "." + getColumn(column) + " = '" + data + "'");
    }

    public void in(SFunction<T, ?> column, List<?> dataList, SQL sql) {
        in(true, getTablePrefix(), column, dataList, sql);
    }

    public void in(boolean use, SFunction<T, ?> column, List<?> dataList, SQL sql) {
        in(use, getTablePrefix(), column, dataList, sql);
    }

    public void in(boolean use, String table, SFunction<T, ?> column, List<?> dataList, SQL sql) {
        if (!use) {
            return;
        }
        String data = dataList.stream().map(k -> "'" + k + "'").collect(Collectors.joining(","));
        sql.WHERE(table + "." + getColumn(column) + " IN(" + data + ") ");
    }

    public void notIn(SFunction<T, ?> column, List<?> dataList, SQL sql) {
        notIn(true, getTablePrefix(), column, dataList, sql);
    }

    public void notIn(boolean use, SFunction<T, ?> column, List<?> dataList, SQL sql) {
        notIn(use, getTablePrefix(), column, dataList, sql);
    }

    public void notIn(boolean use, String table, SFunction<T, ?> column, List<?> dataList, SQL sql) {
        if (!use) {
            return;
        }
        String data = dataList.stream().map(k -> "'" + k + "'").collect(Collectors.joining(","));
        sql.WHERE(table + "." + getColumn(column) + " NOT IN(" + data + ") ");
    }

    public void moreThan(boolean use, SFunction<T, ?> column, String data, String table, SQL sql) {
        if (!use) {
            return;
        }
        sql.WHERE(table + "." + getColumn(column) + " >= '" + data + "'");
    }

    public void lessThan(boolean use, SFunction<T, ?> column, String data, String table, SQL sql) {
        if (!use) {
            return;
        }
        sql.WHERE(table + "." + getColumn(column) + " <= '" + data + "'");
    }

    public String getColumn(SFunction<T, ?> column) {
        LambdaMeta meta = LambdaUtils.extract(column);
        String columnGet = PropertyNamer.methodToProperty(meta.getImplMethodName());
        return ChartUtils.camelToUnderline(columnGet.replace("get", ""), 1);
    }

    /**
     * 将若干条件使用 OR 连接并加括号包裹
     */
    public void or(SQL sql, String... condition) {

        String concat = Arrays.stream(condition).filter(StringUtils::isNotEmpty).collect(Collectors.joining(" OR "));
        sql.WHERE("(" + concat + ")");
    }
}
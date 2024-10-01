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

    /**
     * 别名
     */
    private static final String TABLE_PREFIX = "prefix_";

    /**
     * 实例
     */
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

    /**
     * 获取表名
     * @return 表名
     */
    public String getTableName() {

        TableName annotation = currentModelClass().getAnnotation(TableName.class);
        return annotation.value();
    }

    /**
     * 获取别名
     * @return 别名
     */
    public String getTablePrefix() {

        return TABLE_PREFIX + getTableName();
    }

    /**
     * 获取打了 @TableField 注解的字段
     * @return k: 字段名，v:
     */
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

    /**
     * select
     *
     * @param sql               需要附加的 sql
     * @param excludeFieldSet   需要排除的字段
     */
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

    public void sort(SQL sql, List<OrderItemDTO> orderList, List<BaseProvider<? extends BaseEntity>> baseProviderList) {
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

    public String notLike(SQL sql, SFunction<T, ?> column, String data) {
        return notLike(sql, true, column, data);
    }

    public String notLike(SQL sql, boolean use, SFunction<T, ?> column, String data) {
        if (!use) {
            return "";
        }
        String conditions = getTablePrefix() + "." + getColumn(column) + " NOT LIKE '%" + data + "%'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String like(SQL sql, SFunction<T, ?> column, String data) {
        return like(sql, true, column, data);
    }

    public String like(SQL sql, boolean use, SFunction<T, ?> column, String data) {
        if (!use) {
            return "";
        }
        String conditions = getTablePrefix() + "." + getColumn(column) + " LIKE '%" + data + "%'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String isNotNull(SQL sql, String table, SFunction<T, ?> column) {
        String conditions = table + "." + getColumn(column) + " IS NOT NULL ";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String isNull(SQL sql, String table, SFunction<T, ?> column) {
        String conditions = table + "." + getColumn(column) + " IS NULL ";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String ne(SQL sql, SFunction<T, ?> column, String data) {
        return ne(sql, true, column, data);
    }

    public String ne(SQL sql, boolean use, SFunction<T, ?> column, String data) {
        if (!use) {
            return "";
        }
        String conditions = getTablePrefix() + "." + getColumn(column) + " != '" + data + "'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String eq(SQL sql, SFunction<T, ?> column, String data) {
        return eq(sql, true, column, data);
    }

    public String eq(SQL sql, boolean use, SFunction<T, ?> column, String data) {
        if (!use) {
            return "";
        }
        String conditions = getTablePrefix() + "." + getColumn(column) + " = '" + data + "'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String in(SQL sql, SFunction<T, ?> column, List<?> dataList) {
        return in(sql, true, getTablePrefix(), column, dataList);
    }

    public String in(SQL sql, boolean use, SFunction<T, ?> column, List<?> dataList) {
        return in(sql, use, getTablePrefix(), column, dataList);
    }

    public String in(SQL sql, boolean use, String table, SFunction<T, ?> column, List<?> dataList) {
        if (!use) {
            return "";
        }
        String data = dataList.stream().map(k -> "'" + k + "'").collect(Collectors.joining(","));
        String conditions = table + "." + getColumn(column) + " IN(" + data + ") ";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String notIn(SQL sql, SFunction<T, ?> column, List<?> dataList) {
        return notIn(sql, true, getTablePrefix(), column, dataList);
    }

    public String notIn(SQL sql, boolean use, SFunction<T, ?> column, List<?> dataList) {
        return notIn(sql, use, getTablePrefix(), column, dataList);
    }

    public String notIn(SQL sql, boolean use, String table, SFunction<T, ?> column, List<?> dataList) {
        if (!use) {
            return "";
        }
        String data = dataList.stream().map(k -> "'" + k + "'").collect(Collectors.joining(","));
        String conditions = table + "." + getColumn(column) + " NOT IN(" + data + ") ";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String moreThan(SQL sql, boolean use, SFunction<T, ?> column, String data, String table) {
        if (!use) {
            return "";
        }
        String conditions = table + "." + getColumn(column) + " >= '" + data + "'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
    }

    public String lessThan(SQL sql, boolean use, SFunction<T, ?> column, String data, String table) {
        if (!use) {
            return "";
        }
        String conditions = table + "." + getColumn(column) + " <= '" + data + "'";
        ConditionUtils.of(sql, Objects::nonNull).handle(k -> sql.WHERE(conditions));
        return conditions;
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
package cc.xiaoxu.cloud.core.utils;

import cc.xiaoxu.cloud.bean.dto.OrderItemDTO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
import cc.xiaoxu.cloud.core.dao.BaseProvider;
import cc.xiaoxu.cloud.core.utils.text.ChartUtils;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SQL 工具类
 *
 * @author 小徐
 * @since 2022/12/8 15:53
 */
public class ProviderUtils {

    public static String asc(boolean asc) {

        return asc ? " ASC" : " DESC";
    }

    public static <T> String stringConcat(Collection<T> coll) {

        return coll.stream()
                .map(k -> "'" + k.toString() + "'")
                .collect(Collectors.joining(","));
    }

    public static void chineseSort(SQL sql, String columnName, boolean asc) {

        sql.ORDER_BY("CONVERT( " + columnName + " USING gbk ) COLLATE gbk_chinese_ci" + asc(asc));
    }

    /**
     * 附加排序
     *
     * @param orderList    排序列表
     * @param sql          需要附加的 sql
     * @param excludeMap   排除列表
     * @param baseProvider 引用的表
     */
    @SafeVarargs
    public static void appendSort(List<OrderItem> orderList, SQL sql, Map<Class<? extends BaseProvider<?>>, Set<String>> excludeMap, Class<? extends BaseProvider<?>>... baseProvider) {

        if (CollectionUtils.isEmpty(orderList)) {
            orderList = getDefaultSort();
        }
        for (OrderItem order : orderList) {
            String columnUnderline = ChartUtils.camelToUnderline(order.getColumn(), 1);
            String suffix = chooseSuffix(columnUnderline, excludeMap, baseProvider);
            String column = suffix + (StringUtils.isNotBlank(suffix) ? "." : "") + columnUnderline;
            switch (order.getColumn()) {
                // 姓名、年级、学校特殊处理
                case "schoolName", "name", "grade" -> chineseSort(sql, column, order.isAsc());
                // 其他
                default -> sql.ORDER_BY(column + asc(order.isAsc()));
            }
        }
    }

    /**
     * 选取前缀
     *
     * @param column       字段
     * @param excludeMap   排除列表
     * @param baseProvider 表对应类
     * @return 选择的前缀
     */
    @SafeVarargs
    @SneakyThrows
    public static String chooseSuffix(String column, Map<Class<? extends BaseProvider<?>>, Set<String>> excludeMap, Class<? extends BaseProvider<?>>... baseProvider) {

        for (Class<? extends BaseProvider<?>> aClass : baseProvider) {
            Set<String> excludeFieldSet = excludeMap.getOrDefault(aClass, new HashSet<>());
            for (String tableField : getTableField(aClass)) {
                if (excludeFieldSet.contains(tableField)) {
                    continue;
                }
                if (column.equals(tableField)) {
                    return getTableSuffix(aClass);
                }
            }
        }
        return "";
    }

    /**
     * 获取字段数组
     *
     * @param baseProvider 类
     * @return 结果
     */
    @SneakyThrows
    public static List<String> getTableField(Class<? extends BaseProvider<?>> baseProvider) {

        Method getFieldMap1 = baseProvider.getMethod("getColumnList");
        return (List<String>) getFieldMap1.invoke(baseProvider);
    }

    /**
     * 获取表名
     *
     * @param baseProvider 类
     * @return 结果
     */
    @SneakyThrows
    public static String getTableName(Class<? extends BaseProvider<?>> baseProvider) {

        Field field = baseProvider.getField("TABLE_NAME");
        return (String) field.get(baseProvider);
    }

    /**
     * 获取表别名
     *
     * @param baseProvider 类
     * @return 结果
     */
    @SneakyThrows
    public static String getTableSuffix(Class<? extends BaseProvider<?>> baseProvider) {

        Field field = baseProvider.getField("TABLE_SUFFIX");
        return (String) field.get(baseProvider);
    }

    /**
     * 获取属性
     *
     * @param baseProvider 类
     * @param fieldName    属性名
     * @param clazz        需要转换成的类型.class
     * @param <T>          需要转换成的类型
     * @return 结果
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static <T> T getTableFields(Class<? extends BaseProvider<?>> baseProvider, String fieldName, Class<T> clazz) {

        Field field = baseProvider.getField(fieldName);
        return (T) field.get(baseProvider);
    }

    public static void appendDefaultSort(PageDTO dto) {

        if (CollectionUtils.isEmpty(dto.getOrders())) {
            dto.setOrders(OrderItemDTO.getDefaultSort());
        } else {
            if (dto.getOrders().size() == 1) {
                if (StringUtils.isEmpty(dto.getOrders().get(0).getColumn())) {
                    dto.setOrders(OrderItemDTO.getDefaultSort());
                }
            }
        }
    }

    public static void appendScope(SQL sql, String data, String symbol, String tableFieldName, String beanName, boolean whereOrHaving) {

        if (StringUtils.isNotBlank(data) && StringUtils.isNotBlank(symbol)) {
            String sqlString = "";
            switch (symbol) {
                case "＞", ">" -> sqlString = tableFieldName + " > #{" + beanName + "}";
                case "≥", ">=" -> sqlString = tableFieldName + " >= #{" + beanName + "}";
                case "＜", "<" -> sqlString = tableFieldName + " < #{" + beanName + "}";
                case "≤", "<=" -> sqlString = tableFieldName + " <= #{" + beanName + "}";
                case "＝", "=" -> sqlString = tableFieldName + " = #{" + beanName + "}";
                case "≠", "!=" -> sqlString = tableFieldName + " != #{" + beanName + "}";
                default -> {
                }
            }
            if (whereOrHaving && StringUtils.isNotBlank(sqlString)) {
                sql.WHERE(sqlString);
            } else {
                sql.HAVING(sqlString);
            }
        }
    }

    public static List<OrderItem> getDefaultSort() {
        List<OrderItem> orderList = new ArrayList<>();
        OrderItem orderItem = new OrderItem();
        orderItem.setAsc(false);
        orderItem.setColumn("id");
        orderList.add(orderItem);
        return orderList;
    }

    public static void appendTimeSlot(SQL sql, String timeStart, String timeEnd, String databaseTimeStart, String databaseTimeEnd) {

        sql.WHERE(databaseTimeEnd + " >= #{" + timeStart + "}");
        sql.WHERE("#{" + timeEnd + "} >= " + databaseTimeStart);
    }
}
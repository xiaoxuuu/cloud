package cc.xiaoxu.cloud.core.utils.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * <p>日期基础工具类：日期转换</p>
 *
 * @author 小徐
 * @since 2025/6/20 17:17
 */
public class BasicDateUtils {

    /**
     * 默认时区
     */
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 年月日时分秒
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 禁止实例化
     */
    BasicDateUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 将常见的日期/时间类自动转换为 {@link LocalDateTime}
     *
     * @param obj 参数
     * @return 结果
     * @throws IllegalArgumentException 如果 obj 为 null 或不支持的类型
     */
    public static LocalDateTime toLocalDateTime(Object obj) {

        if (null == obj) {
            throw new IllegalArgumentException("obj can not be null");
        }
        return switch (obj) {
            case String s -> stringToLocalDateTime(s, DEFAULT_DATE_TIME_FORMAT);
            // LocalDateTime 直接返回
            case LocalDateTime o -> o;
            // LocalDate 转换为当天的午夜时间
            case LocalDate o -> o.atStartOfDay();
            case java.sql.Date o -> o.toLocalDate().atStartOfDay();
            case java.sql.Timestamp o -> o.toLocalDateTime();
            case Date o -> LocalDateTime.ofInstant(o.toInstant(), DEFAULT_ZONE_ID);
            case Instant o -> LocalDateTime.ofInstant(o, DEFAULT_ZONE_ID);
            default -> throw new IllegalArgumentException("暂不支持的数据类型: " + obj.getClass().getName());
        };
    }

    public static LocalDateTime stringToLocalDateTime(String str, String pattern) {

        if (null == str) {
            throw new IllegalArgumentException("str can not be null");
        }
        try {
            return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
        } catch (java.time.DateTimeException e) {
            LocalDate localDate = stringToLocalDate(str, pattern);
            return toLocalDateTime(localDate);
        }
    }

    public static LocalDate stringToLocalDate(String str, String pattern) {

        if (null == str) {
            throw new IllegalArgumentException("str can not be null");
        }
        return LocalDate.parse(str, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * {@link LocalDateTime LocalDateTime} 转 {@link Date Date}
     *
     * @param localDateTime 日期
     * @return 转换的 {@link Date Date}
     */
    public static Date toDate(LocalDateTime localDateTime) {

        ZonedDateTime zdt = localDateTime.atZone(DEFAULT_ZONE_ID);
        return Date.from(zdt.toInstant());
    }

    /**
     * {@link Date Date} 转 {@link String String}
     *
     * @param date    指定时间
     * @param pattern 样式
     * @return 结果
     */
    public static String toString(Object date, String pattern) {

        return toLocalDateTime(date).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * {@link Date Date} 转 {@link String String}
     *
     * @param date 指定时间
     * @return 结果
     */
    public static Long toTimestamp(Object date) {

        return toLocalDateTime(date).toInstant(ZoneOffset.UTC).toEpochMilli();
    }
}
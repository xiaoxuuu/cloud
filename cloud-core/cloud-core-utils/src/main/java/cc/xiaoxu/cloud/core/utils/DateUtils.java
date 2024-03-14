package cc.xiaoxu.cloud.core.utils;

import cc.xiaoxu.cloud.core.utils.constants.DateConstants;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>日期工具类</p>
 * <p>时区使用系统时区</p>
 *
 * @author 小徐
 * @since 2023/1/3 16:51
 */
public class DateUtils {

    /**
     * 获取系统默认时区
     */
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    /**
     * 禁止实例化
     */
    private DateUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * {@link Date Date} 转 {@link LocalDateTime LocalDateTime}
     *
     * @param date 日期
     * @return 转换的 {@link LocalDateTime LocalDateTime}
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {

        Instant instant = date.toInstant();
        return instant.atZone(ZONE_ID).toLocalDateTime();
    }

    /**
     * {@link LocalDateTime LocalDateTime} 转 {@link Date Date}
     *
     * @param localDateTime 日期
     * @return 转换的 {@link Date Date}
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {

        ZonedDateTime zdt = localDateTime.atZone(ZONE_ID);
        return Date.from(zdt.toInstant());
    }

    /**
     * {@link String String} 转 {@link LocalDateTime LocalDateTime}
     *
     * @param date    日期
     * @param pattern 日期格式，参考：{@link cc.xiaoxu.cloud.core.utils.constants.DateConstants DateConstants}
     * @return 转换结果
     */
    public static LocalDateTime stringToLocalDateTime(String date, String pattern) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * {@link String String} 转 {@link LocalDateTime LocalDateTime}
     *
     * @param date 日期，默认格式：{@link cc.xiaoxu.cloud.core.utils.constants.DateConstants#DEFAULT_DATE_TIME_FORMAT DateConstants.DEFAULT_DATE_FORMAT}
     * @return 转换结果
     */
    public static LocalDateTime stringToLocalDateTime(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateConstants.DEFAULT_DATE_TIME_FORMAT);
        return LocalDateTime.parse(date, formatter);
    }

    /**
     * {@link LocalDateTime LocalDateTime} 转 {@link String String}
     *
     * @param localDateTime 日期
     * @param pattern       日期格式，参考：{@link cc.xiaoxu.cloud.core.utils.constants.DateConstants DateConstants}
     * @return 转换结果
     */
    public static String localDateTimeToString(LocalDateTime localDateTime, String pattern) {

        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * {@link LocalDateTime LocalDateTime} 转 {@link String String}
     *
     * @param localDateTime 日期，默认格式：{@link cc.xiaoxu.cloud.core.utils.constants.DateConstants#DEFAULT_DATE_TIME_FORMAT DateConstants.DEFAULT_DATE_FORMAT}
     * @return 转换结果
     */
    public static String localDateTimeToString(LocalDateTime localDateTime) {

        return localDateTime.format(DateTimeFormatter.ofPattern(DateConstants.DEFAULT_DATE_TIME_FORMAT));
    }

    /**
     * {@link Date Date} 转 {@link String String}
     *
     * @param date    指定时间
     * @param pattern 样式
     * @return 结果
     */
    public static String dateToString(Date date, String pattern) {

        return localDateTimeToString(dateToLocalDateTime(date), pattern);
    }

    /**
     * {@link String String} 转 {@link Date Date}
     *
     * @param date    日期
     * @param pattern 格式化字符串
     * @return 结果
     */
    public static Date stringToDate(String date, String pattern) {

        return localDateTimeToDate(stringToLocalDateTime(date, pattern));
    }

    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 获取当前日期
     *
     * @param pattern 格式
     * @return 日期
     */
    public static String getCurrent(String pattern) {
        return localDateTimeToString(LocalDateTime.now(), DateConstants.YEAR);
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static String getNowString() {
        return localDateTimeToString(LocalDateTime.now(), DateConstants.DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static Date getNowDate() {
        return localDateTimeToDate(LocalDateTime.now());
    }

    /**
     * 获取当前时间
     *
     * @param pattern 样式
     * @return 当前时间
     */
    public static String getNowString(String pattern) {
        return localDateTimeToString(LocalDateTime.now(), pattern);
    }

    /**
     * 获取最近几年年份
     *
     * @param someYear 0 为今年。1 为去年 + 今年，以此类推
     * @return 最近几年年份
     */
    public static List<Integer> getLastSomeYear(int someYear) {

        return getLastSomeYear(someYear, Integer.parseInt(getCurrent(DateConstants.YEAR)));
    }

    /**
     * 获取最近几年年份，根据指定年份倒推
     *
     * @param someYear   指定年份
     * @param targetYear 目标年份
     * @return 指定年份与目标年份的年份列表
     */
    public static List<Integer> getLastSomeYear(int someYear, int targetYear) {

        if (someYear < 0) {
            return new ArrayList<>();
        }
        List<Integer> yearList = new ArrayList<>(someYear + 1);
        for (int i = 0; i <= someYear; i++) {
            yearList.add(targetYear - i);
        }
        return yearList;
    }

    /**
     * <p>比较时间大小，会自动对时间进行转换</p>
     *
     * @param l1 时间1
     * @param l2 时间2
     * @return {@code 时间1 > 时间2 = true}
     */
    public static boolean moreThan(Object l1, Object l2) {

        return cast(l1).isAfter(cast(l2));
    }

    /**
     * <p>将常见日期自动转换为 {@link LocalDateTime LocalDateTime}</p>
     * <p>{@link String String} 类型默认使用 {@link DateUtils#stringToLocalDateTime(String date) XDateUtils.stringToLocalDateTime(String date)} 方法</p>
     *
     * @param o 日期
     * @return 结果
     */
    public static LocalDateTime cast(Object o) {

        if (null == o) {
            return null;
        } else if (o instanceof String) {
            return stringToLocalDateTime((String) o);
        } else if (o instanceof Date) {
            return dateToLocalDateTime((Date) o);
        } else if (o instanceof LocalDateTime) {
            return (LocalDateTime) o;
        } else if (o instanceof Instant) {
            return ((Instant) o).atZone(ZONE_ID).toLocalDateTime();
        } else if (o instanceof LocalDate) {
            return ((LocalDate) o).atStartOfDay(ZONE_ID).toLocalDateTime();
        } else if (o instanceof ZonedDateTime) {
            return ((ZonedDateTime) o).toLocalDateTime();
        } else {
            throw new RuntimeException("日期转换时遇到暂不支持的数据类型");
        }
    }
}
package cc.xiaoxu.cloud.core.utils.date;

import cc.xiaoxu.cloud.core.utils.constants.DateConstants;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <p>日期工具类</p>
 * <p>时区使用系统时区</p>
 *
 * @author 小徐
 * @since 2023/1/3 16:51
 */
public class DateUtils extends BasicDateUtils {

    /**
     * 禁止实例化
     */
    private DateUtils() {
        super();
        throw new IllegalAccessError(this.getClass().getName());
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

        return convertToLocalDateTime(date).format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 获取当前日期
     *
     * @param pattern 格式
     * @return 日期
     */
    public static String now(String pattern) {
        return toString(LocalDateTime.now(), pattern);
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static String now() {
        return now(DateConstants.DEFAULT_DATE_TIME_FORMAT);
    }

    /**
     * 获取最近几年年份
     *
     * @param someYear 0 为今年。1 为去年 + 今年，以此类推
     * @return 最近几年年份
     */
    public static List<Integer> getLastSomeYear(int someYear) {

        return getLastSomeYear(someYear, Integer.parseInt(now(DateConstants.YEAR)));
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

        if (Objects.isNull(l1) || Objects.isNull(l2)) {
            return false;
        }
        return convertToLocalDateTime(l1).isAfter(convertToLocalDateTime(l2));
    }
}
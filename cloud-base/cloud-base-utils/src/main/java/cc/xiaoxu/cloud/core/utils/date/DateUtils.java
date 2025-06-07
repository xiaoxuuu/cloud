package cc.xiaoxu.cloud.core.utils.date;

import cc.xiaoxu.cloud.core.utils.constants.DateConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
     * 获取最近几年年份
     *
     * @param someYear 0 为今年。1 为去年 + 今年，以此类推
     * @return 最近几年年份
     */
    public static List<Integer> getLastSomeYear(int someYear) {

        return getLastSomeYear(someYear, Integer.parseInt(toString(LocalDateTime.now(), DateConstants.YEAR)));
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
     * @param o1 时间1
     * @param o2 时间2
     * @return {@code o1 > o2 = true}
     */
    public static boolean moreThan(Object o1, Object o2) {

        if (Objects.isNull(o1) || Objects.isNull(o2)) {
            return false;
        }
        return toLocalDateTime(o1).isAfter(toLocalDateTime(o2));
    }

    /**
     * 查询两日期中的每天，两端闭区间
     *
     * @param o1      第一个日期
     * @param o2      第二个日期
     * @param pattern 输出的格式
     * @return 两日期包含的每天
     */
    public static List<String> findEveryDay(Object o1, Object o2, String pattern) {

        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Input parameters cannot be null or empty.");
        }

        LocalDateTime left = toLocalDateTime(o1);
        LocalDateTime right = toLocalDateTime(o2);
        if (left.isAfter(right)) {
            LocalDateTime temp = left;
            left = right;
            right = temp;
        }

        List<String> dates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime current = left;
        while (!current.isAfter(right)) {
            String formattedDate = current.format(formatter);
            dates.add(formattedDate);
            current = current.plusDays(1);
        }
        return dates;
    }

    /**
     * 计算两个 LocalDateTime 对象之间的时间差（以毫秒为单位）。
     *
     * @param stDate  起始 LocalDateTime 对象，不能为 null。
     * @param endDate 结束 LocalDateTime 对象，不能为 null。
     * @return 两个日期之间的时间差，以毫秒为单位。
     * @throws IllegalArgumentException 如果 stDate 或 endDate 为 null。
     */
    public static long timeDifference(LocalDateTime stDate, LocalDateTime endDate) {

        return timeDifference(stDate, endDate, ChronoUnit.MILLIS);
    }

    /**
     * 计算两个 LocalDateTime 对象之间的时间差（以指定的单位）。
     *
     * @param o1   起始 LocalDateTime 对象，不能为 null。
     * @param o2   结束 LocalDateTime 对象，不能为 null。
     * @param unit 时间单位，例如 ChronoUnit.MILLIS, ChronoUnit.SECONDS, ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS. 不能为 null。
     * @return 两个日期之间的时间差，以指定单位为单位。
     * @throws IllegalArgumentException 如果 stDate 或 endDate 或 unit 为 null。
     */
    public static long timeDifference(Object o1, LocalDateTime o2, ChronoUnit unit) {

        if (unit == null) {
            throw new IllegalArgumentException("unit cannot be null.");
        }
        LocalDateTime stDate = toLocalDateTime(o1);
        LocalDateTime endDate = toLocalDateTime(o1);
        return unit.between(stDate, endDate);
    }
}
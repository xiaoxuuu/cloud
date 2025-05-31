package cc.xiaoxu.cloud.core.utils.date;

import cc.xiaoxu.cloud.core.utils.constants.DateConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        return convertToLocalDateTime(o1).isAfter(convertToLocalDateTime(o2));
    }

    public static List<String> findEveryDay(Object o1, Object o2, String pattern) {

        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("Input parameters cannot be null or empty.");
        }

        LocalDateTime left = convertToLocalDateTime(o1);
        LocalDateTime right = convertToLocalDateTime(o2);
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
}
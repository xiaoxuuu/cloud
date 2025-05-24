package cc.xiaoxu.cloud.core.utils.date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    public static LocalDateTime convertToLocalDateTime(Object obj) {

        if (null == obj) {
            throw new IllegalArgumentException("obj can not be null");
        }
        return switch (obj) {
            case String s -> convertToLocalDateTime(s, DEFAULT_DATE_TIME_FORMAT);
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

    public static LocalDateTime convertToLocalDateTime(String str, String pattern) {

        if (null == str) {
            throw new IllegalArgumentException("str can not be null");
        }
        return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(pattern));
    }
}
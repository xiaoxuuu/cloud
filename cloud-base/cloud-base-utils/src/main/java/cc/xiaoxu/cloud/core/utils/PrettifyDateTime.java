package cc.xiaoxu.cloud.core.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>日期美化工具类</p>
 *
 * @author 小徐
 * @since 2023/1/3 17:32
 */
public class PrettifyDateTime {

    /**
     * 时间，纳秒
     */
    private Long time;

    /**
     * 使用中文单位
     */
    private boolean chineseUnit;

    /**
     * 拼接符号
     */
    private String appendSymbol;

    /**
     * 舍去的单位
     */
    private List<TimeUnit> removeUnitList;

    /**
     * 禁止实例化
     */
    private PrettifyDateTime() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 默认构造
     *
     * @param time 时间（纳秒）
     */
    private PrettifyDateTime(Long time) {

        init();
        this.time = time;
    }

    /**
     * 使用纳秒构建
     *
     * @param ns 纳秒
     * @return this
     */
    public static PrettifyDateTime initWithByNano(Long ns) {

        return new PrettifyDateTime(ns);
    }

    /**
     * 使用微秒构建
     *
     * @param ms 微秒
     * @return this
     */
    public static PrettifyDateTime initWithMicrosecond(Long ms) {

        return new PrettifyDateTime(ms * 1000);
    }

    /**
     * 使用毫秒构建
     *
     * @param ms 秒
     * @return this
     */
    public static PrettifyDateTime initWithMillisecond(Long ms) {

        return new PrettifyDateTime(ms * 1000 * 1000);
    }

    /**
     * 使用秒构建
     *
     * @param s 秒
     * @return this
     */
    public static PrettifyDateTime initWithSecond(Long s) {

        return new PrettifyDateTime(s * 1000 * 1000 * 1000);
    }

    /**
     * 使用指定时间与当前时间的差值构建
     *
     * @param dateTime 时间
     * @return this
     */
    public static PrettifyDateTime untilNow(LocalDateTime dateTime) {

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        return new PrettifyDateTime(duration.toNanos());
    }

    /**
     * 初始化
     */
    private void init() {

        this.time = 0L;
        this.chineseUnit = true;
        this.appendSymbol = " ";
        this.removeUnitList = new ArrayList<>();
    }

    /**
     * 设置拼接字符串
     *
     * @param symbol 拼接字符串
     * @return this
     */
    public PrettifyDateTime symbol(String symbol) {

        this.appendSymbol = symbol;
        return this;
    }

    /**
     * 设置需要舍去的单位
     *
     * @param timeUnit 待舍去的单位
     * @return this
     */
    public PrettifyDateTime removeUnit(TimeUnit timeUnit) {

        this.removeUnitList.add(timeUnit);
        return this;
    }

    /**
     * <p>将纳秒值进行单位转换后美化输出</p>
     * <p>例：{@code 1 --> 1纳秒}</p>
     * <p>例：{@code 90_060_001_001_001L --> 1天 1时 1分 1毫秒 1微秒 1纳秒}</p>
     *
     * @return 美化后的数据
     */
    public String prettify() {

        int nanos = 1000;
        int millis = nanos * 1000;
        int sec = millis * 1000;
        long mi = sec * 60L;
        long hh = mi * 60;
        long dd = hh * 24;

        long day = time / dd;
        long hour = (time - day * dd) / hh;
        long minute = (time - day * dd - hour * hh) / mi;
        long second = (time - day * dd - hour * hh - minute * mi) / sec;
        long millisecond = (time - day * dd - hour * hh - minute * mi - second * sec) / millis;
        long microsecond = (time - day * dd - hour * hh - minute * mi - second * sec - millisecond * millis) / nanos;
        long nanosecond = time - day * dd - hour * hh - minute * mi - second * sec - millisecond * millis - microsecond * nanos;

        List<String> list = new ArrayList<>(7);
        appendUnit(TimeUnit.DAY, day, list);
        appendUnit(TimeUnit.HOUR, hour, list);
        appendUnit(TimeUnit.MINUTE, minute, list);
        appendUnit(TimeUnit.SECOND, second, list);
        appendUnit(TimeUnit.MILLISECOND, millisecond, list);
        appendUnit(TimeUnit.MICROSECOND, microsecond, list);
        appendUnit(TimeUnit.NANOSECOND, nanosecond, list);
        return String.join(appendSymbol, list);
    }

    /**
     * 附加结果
     *
     * @param timeUnit 附加的单位
     * @param time     具体时间
     * @param list     待附加的结果集
     */
    private void appendUnit(TimeUnit timeUnit, long time, List<String> list) {

        if (!removeUnitList.contains(timeUnit) && time > 0) {
            list.add(time + (chineseUnit ? timeUnit.getDesc() : timeUnit.getName()));
        }
    }

    enum TimeUnit {

        /**
         * 时间单位枚举
         */
        DAY("day", "天"),
        HOUR("hour", "时"),
        MINUTE("minute", "分"),
        SECOND("day", "秒"),
        MILLISECOND("millisecond", "毫秒"),
        MICROSECOND("microsecond", "微秒"),
        NANOSECOND("nanosecond", "纳秒"),
        ;

        private final String name;
        private final String desc;

        TimeUnit(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }
}
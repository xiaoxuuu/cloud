package cc.xiaoxu.cloud.core.utils;

import cc.xiaoxu.cloud.core.bean.func.FunctionHandler;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.math.MathUtils;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * <p>StopWatch 输出优化</p>
 * <p>
 * 2022/7/6 下午 04:48
 *
 * @author 小徐
 */
public class StopWatchUtil extends StopWatch {

    private FunctionHandler lineHandler;
    private Boolean printLog = false;

    public StopWatchUtil() {
    }

    public StopWatchUtil(String id) {
        super(id);
    }

    public StopWatchUtil(String id, FunctionHandler lineHandler, Boolean printLog) {
        super(id);
        this.lineHandler = lineHandler;
        this.printLog = printLog;
        if (printLog) {
            if (null == lineHandler) {
                throw new CustomException("无可使用日志输出工具");
            }
            lineHandler.handle(id);
        }
    }

    public void start(@NotNull String taskName) {

        if (super.isRunning()) {
            super.stop();
        }
        super.start(taskName);
        if (printLog) {
            lineHandler.handle(taskName);
        }
    }

    public void print() {

        if (super.isRunning()) {
            super.stop();
        }
        if (null != lineHandler) {
            lineHandler.handle(prettyPrintMs(this));
        }
    }

    public void print(FunctionHandler lineHandler) {

        if (super.isRunning()) {
            super.stop();
        }
        lineHandler.handle(prettyPrintMs(this));
    }

    /**
     * @param sw StopWatch
     * @return 固定格式数据
     */
    public static String prettyPrintMs(StopWatch sw) {

        if (Objects.isNull(sw)) {
            return "";
        }

        if (sw.isRunning()) {
            sw.stop();
        }

        // 总纳秒数
        long totalTimeNanos = sw.getTotalTimeNanos();
        // 任务名称
        String stopWatchName = sw.getId();
        // 所有子任务
        StopWatch.TaskInfo[] taskInfoArray = sw.getTaskInfo();
        // 分隔符
        String delimiter = "  ";
        // 换行符
        String lineSeparator = System.lineSeparator();

        // 任务
        List<TaskInfo> taskInfoList = new ArrayList<>();
        for (StopWatch.TaskInfo task : taskInfoArray) {
            TaskInfo taskInfo = new TaskInfo(task.getTimeNanos(), task.getTaskName(), totalTimeNanos, delimiter);
            taskInfoList.add(taskInfo);
        }

        TaskInfo taskHead = new TaskInfo("TimeSpent", "pct.", "TaskName", delimiter);
        TaskInfo taskTotal = new TaskInfo(totalTimeNanos, stopWatchName, totalTimeNanos, delimiter);

        List<List<String>> list = getStream(taskHead, taskInfoList, taskTotal).map(k -> List.of(k.getTime(), k.getPercentage(), k.getTaskName())).toList();
        return TableUtils.formatTable(list);
    }

    private static Stream<TaskInfo> getStream(TaskInfo taskHead, List<TaskInfo> taskInfoList, TaskInfo taskTotal) {
        return Stream.concat( Stream.concat(Stream.of(taskHead),taskInfoList.stream()), Stream.of(taskTotal));
    }

    @Data
    @NoArgsConstructor
    private static class TaskInfo {

        /**
         * 1 标题 2 总任务 3 子任务
         */
        private Integer type;
        private Long timeNanos;
        private String time;
        private String percentage;
        private String taskName;
        private String delimiter;

        public TaskInfo(Long timeNanos, String taskName, Long totalTimeNanos, String delimiter) {
            this.timeNanos = timeNanos;
            this.time = Util.getSimpleFormatTimeNanos(timeNanos);
            // 计算百分比
            BigDecimal divide = totalTimeNanos == 0 ? BigDecimal.ZERO : MathUtils.divide(timeNanos, totalTimeNanos, 4);
            String percentageString = MathUtils.toString(MathUtils.multiply(divide, 100));
            percentageString = percentageString.contains(".") ? percentageString : (percentageString + ".00");
            String[] split = percentageString.split("\\.");
            String integerPart = Util.supplement(split[0], true, 3, " ");
            String decimalPart = Util.supplement(split[1], true, 2, "0");
            this.percentage = integerPart + "." + decimalPart + "%";
            this.taskName = taskName;
            this.delimiter = delimiter;
            this.type = Objects.equals(timeNanos, totalTimeNanos) ? 2 : 3;
        }

        public TaskInfo(String time, String percentage, String taskName, String delimiter) {
            this.time = time;
            this.percentage = percentage;
            this.taskName = taskName;
            this.delimiter = delimiter;
            this.type = 1;
        }
    }

    private static class Util {

        /**
         * @param s      原始数据
         * @param flag   从头部补充
         * @param length 长度
         * @param str    占位符号
         */
        public static String supplement(String s, boolean flag, int length, String str) {

            if (s.length() >= length) {
                return s;
            }
            String s1 = java.lang.String.valueOf(str).repeat(length - s.length());
            if (flag) {
                s1 = s1 + s;
            } else {
                s1 = s + s1;
            }
            return s1;
        }

        public static String getSimpleFormatTimeNanos(Long ns) {

            int nanos = 1000;
            int millis = nanos * 1000;
            int sec = millis * 1000;
            long mi = sec * 60L;
            long hh = mi * 60;
            long dd = hh * 24;

            long day = ns / dd;
            long hour = (ns - day * dd) / hh;
            long minute = (ns - day * dd - hour * hh) / mi;
            long second = (ns - day * dd - hour * hh - minute * mi) / sec;
            long millisecond = (ns - day * dd - hour * hh - minute * mi - second * sec) / millis;
            long microseconds = (ns - day * dd - hour * hh - minute * mi - second * sec - millisecond * millis) / nanos;
            long nanosecond = ns - day * dd - hour * hh - minute * mi - second * sec - millisecond * millis - microseconds * nanos;

            List<String> list = new ArrayList<>();
            if (day > 0) {
                list.add(day + "d");
            }
            if (hour > 0) {
                list.add(hour + "h");
            }
            if (minute > 0) {
                list.add(minute + "m");
            }
            String secStr = second + "." +
                    supplement(String.valueOf(millisecond), true, 3, "0") +
                    supplement(String.valueOf(microseconds), true, 3, "0") +
                    supplement(String.valueOf(nanosecond), true, 3, "0") + "s";
            list.add(secStr);
            return StringUtils.join(list, " ");
        }

        public static String getSimpleFormatTimeMillis(Long ms) {

            int sec = 1000;
            long mi = sec * 60L;
            long hh = mi * 60;
            long dd = hh * 24;

            long day = ms / dd;
            long hour = (ms - day * dd) / hh;
            long minute = (ms - day * dd - hour * hh) / mi;
            long second = (ms - day * dd - hour * hh - minute * mi) / sec;
            long millisecond = ms - day * dd - hour * hh - minute * mi - second * sec;

            List<String> list = new ArrayList<>();
            if (day > 0) {
                list.add(day + "d");
            }
            if (hour > 0) {
                list.add(hour + "h");
            }
            if (minute > 0) {
                list.add(minute + "m");
            }
            String secStr = second + "." + supplement(String.valueOf(millisecond), true, 3, "0") + "s";
            list.add(secStr);
            return StringUtils.join(list, " ");
        }
    }
}
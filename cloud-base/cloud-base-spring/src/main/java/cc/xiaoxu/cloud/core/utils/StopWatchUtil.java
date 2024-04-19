package cc.xiaoxu.cloud.core.utils;

import cc.xiaoxu.cloud.core.bean.func.FunctionHandler;
import cc.xiaoxu.cloud.core.utils.math.MathUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * <p>StopWatch 输出优化</p>
 *
 * 2022/7/6 下午 04:48
 *
 * @author 小徐
 */
public class StopWatchUtil extends StopWatch {

    public StopWatchUtil() {
    }

    public StopWatchUtil(String id) {
        super(id);
    }

    /**
     * 启动计时器时自动停止，并输出任务名
     * @param taskName 任务名
     * @param lineHandler 日志输出函数
     */
    public void start(String taskName, FunctionHandler lineHandler) {

        if (super.isRunning()) {
            super.stop();
        }
        if (null != lineHandler) {
            lineHandler.handle(taskName);
        }
        super.start(taskName);
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

        // 计算总长度
        int timeLength = getStream(taskInfoList, taskHead, taskTotal).map(TaskInfo::getTime).map(StopWatchUtil::getLength).reduce(Integer::max).orElse(0);
        int percentageLength = getStream(taskInfoList, taskHead, taskTotal).map(TaskInfo::getPercentage).map(StopWatchUtil::getLength).reduce(Integer::max).orElse(0);
        int taskNameLength = getStream(taskInfoList, taskHead, taskTotal).map(TaskInfo::getTaskName).map(StopWatchUtil::getLength).reduce(Integer::max).orElse(0);
        int maxLength = timeLength + percentageLength + taskNameLength + delimiter.length() * 2;

        StringBuilder builder = new StringBuilder();

        String head = Util.addMultiple("=", maxLength);
        String body = Util.addMultiple("-", maxLength);
        builder.append(lineSeparator);
        builder.append(head);
        builder.append(lineSeparator);
        builder.append(getLine(taskHead, timeLength, percentageLength, taskNameLength, delimiter));
        builder.append(lineSeparator);
        builder.append(head);
        builder.append(lineSeparator);
        builder.append(getLine(taskTotal, timeLength, percentageLength, taskNameLength, delimiter));
        builder.append(lineSeparator);
        builder.append(body);
        builder.append(lineSeparator);
        for (TaskInfo task : taskInfoList) {
            builder.append(getLine(task, timeLength, percentageLength, taskNameLength, delimiter));
            builder.append(lineSeparator);
        }
        builder.append(head);
        return builder.toString();
    }

    private static int getLength(String str) {

        return str.length() + Util.getHanNum(str);
    }

    private static Stream<TaskInfo> getStream(List<TaskInfo> taskInfoList, TaskInfo taskHead, TaskInfo taskTotal) {
        return Stream.concat(taskInfoList.stream(), Stream.of(taskHead, taskTotal));
    }

    private static String fillBlanks(String str, int length) {

        int i = length - str.length() - Util.getHanNum(str);
        return i == 0 ? str : (str + Util.addMultiple(" ", i));
    }

    private static String getLine(TaskInfo taskInfo, int timeLength, int percentageLength, int taskNameLength, String delimiter) {

        return fillBlanks(taskInfo.getTime(), timeLength) +
                delimiter +
                fillBlanks(taskInfo.getPercentage(), percentageLength) +
                delimiter +
                fillBlanks(taskInfo.getTaskName(), taskNameLength);
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

        public static String addMultiple(Object o, int num) {

            StringBuilder builder = new StringBuilder();
            if (num <= 0) {
                return "";
            }
            for (int i = 0; i < num; i++) {
                builder.append(o.toString());
            }
            return builder.toString();
        }

        public static int getHanNum(String str) {
            int count = 0;
            String regEx = "[\\u4e00-\\u9fa5]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            //boolean b=m.matches();//可判断是否符合正则表达式条件
            // 进行累计汉字数量
            while (m.find()) {
                for (int i = 0; i <= m.groupCount(); i++) {
                    count++;
                }
            }
            return count;
        }

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
            String s1 = String.valueOf(str).repeat(length - s.length());
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
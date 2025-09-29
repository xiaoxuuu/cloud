package cc.xiaoxu.cloud.core.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表格工具类，用于格式化表格数据为字符串
 *
 * @author 小徐
 */
public class TableUtils {

    /**
     * 将表格数据格式化为字符串形式
     *
     * @param data 表格数据，List<List<String>>，第一行通常为表头
     * @return 格式化后的表格字符串
     */
    public static String formatTable(List<List<String>> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }

        // 换行符
        String lineSeparator = System.lineSeparator();
        // 分隔符
        String delimiter = "  ";
        
        // 计算每列的最大宽度
        int columnCount = data.getFirst().size();
        int[] timeLengths = new int[columnCount];
        int[] percentageLengths = new int[columnCount];
        int[] taskNameLengths = new int[columnCount];
        
        // 初始化最大长度数组
        for (int i = 0; i < columnCount; i++) {
            timeLengths[i] = 0;
            percentageLengths[i] = 0;
            taskNameLengths[i] = 0;
        }
        
        // 遍历所有行计算每列最大宽度
        for (List<String> row : data) {
            for (int i = 0; i < Math.min(row.size(), columnCount); i++) {
                String cell = row.get(i) == null ? "" : row.get(i);
                int timeLength = getLength(cell);
                if (timeLength > timeLengths[i]) {
                    timeLengths[i] = timeLength;
                }
            }
        }
        
        // 计算总长度
        int maxLength = 0;
        for (int i = 0; i < columnCount; i++) {
            maxLength += timeLengths[i];
        }
        maxLength += delimiter.length() * (columnCount - 1);
        
        StringBuilder builder = new StringBuilder();
        
        String head = addMultiple("=", maxLength);
        String body = addMultiple("-", maxLength);
        builder.append(lineSeparator);
        builder.append(head);
        builder.append(lineSeparator);
        
        // 添加每一行数据
        for (int i = 0; i < data.size(); i++) {
            List<String> row = data.get(i);
            
            // 构建行数据
            StringBuilder lineBuilder = new StringBuilder();
            for (int j = 0; j < columnCount; j++) {
                String cell = j < row.size() ? (row.get(j) == null ? "" : row.get(j)) : "";
                lineBuilder.append(fillBlanks(cell, timeLengths[j]));
                if (j < columnCount - 1) {
                    lineBuilder.append(delimiter);
                }
            }
            builder.append(lineBuilder.toString());
            builder.append(lineSeparator);
            
            // 在表头后添加分隔线
            if (i == 0) {
                builder.append(body);
                builder.append(lineSeparator);
            }
        }
        
        builder.append(head);
        return builder.toString();
    }
    
    /**
     * 计算字符串的真实长度（考虑中文字符）
     *
     * @param str 字符串
     * @return 真实长度
     */
    private static int getLength(String str) {
        return str.length() + getHanNum(str);
    }
    
    /**
     * 统计字符串中中文字符的数量
     *
     * @param str 字符串
     * @return 中文字符数量
     */
    private static int getHanNum(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        // 进行累计汉字数量
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 用指定字符填充指定次数
     *
     * @param o   填充字符
     * @param num 填充次数
     * @return 填充后的字符串
     */
    private static String addMultiple(Object o, int num) {
        StringBuilder builder = new StringBuilder();
        if (num <= 0) {
            return "";
        }
        for (int i = 0; i < num; i++) {
            builder.append(o.toString());
        }
        return builder.toString();
    }
    
    /**
     * 用空格填充字符串到指定长度
     *
     * @param str    原始字符串
     * @param length 目标长度
     * @return 填充后的字符串
     */
    private static String fillBlanks(String str, int length) {
        int i = length - str.length() - getHanNum(str);
        return i == 0 ? str : (str + addMultiple(" ", i));
    }
    
    /**
     * 示例方法，演示如何使用formatTable方法
     */
    public static void main(String[] args) {
        // 创建示例数据
        List<List<String>> tableData = List.of(
                List.of("TimeSpent", "pct.", "TaskName"),
                List.of("100ms", "50%", "Task1"),
                List.of("200ms", "100%", "Task2 中文")
        );
        
        // 格式化并打印表格
        String formattedTable = formatTable(tableData);
        System.out.println(formattedTable);
    }
}
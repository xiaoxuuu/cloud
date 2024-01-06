package cc.xiaoxu.cloud.core.utils.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 * <p>
 * 2022/11/9 15:04
 *
 * @author XiaoXu
 */
public class StringUtils {

    /**
     * 禁止实例化
     */
    private StringUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * <p>判断字符串是否为空</p>
     *
     * @param str 参数
     * @return 字符串返回 true
     */
    public static boolean isBlank(String str) {

        return null == str || str.trim().isEmpty();
    }

    /**
     * <p>是否包含字符串</p>
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <p>去空格</p>
     *
     * @param str 源字符串
     * @return 结果
     */
    public static String trim(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * <p>将字符串重复指定次数</p>
     *
     * @param o   源数据
     * @param num 重复次数
     * @return 结果
     */
    public static String addMultiple(Object o, int num) {

        StringBuilder builder = new StringBuilder();
        if (num <= 0) {
            return "";
        }
        if (1 == num) {
            return o.toString();
        }
        for (int i = 0; i < num; i++) {
            builder.append(o.toString());
        }
        return builder.toString();
    }

    /**
     * <p>获取字符串中汉字数量</p>
     *
     * @param str 源字符串
     * @return 数量
     */
    public static int getHanNum(String str) {
        int count = 0;
        String regEx = "[\\u4e00-\\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        // boolean b = m.matches();
        // 可判断是否符合正则表达式条件
        // 进行累计汉字数量
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count++;
            }
        }
        return count;
    }

    /**
     * <p>字符串长度对齐</p>
     * <p>可长度不一的字符串首部填充空格，使其长度尽可能相等</p>
     *
     * @param s      原始数据
     * @param flag   从头部补充
     * @param length 长度
     * @param str    占位符号
     * @return 补充后的字符串
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
}
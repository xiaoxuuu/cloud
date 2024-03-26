package cc.xiaoxu.cloud.core.utils.text;

/**
 * <p>字符工具</p>
 *
 * @author 小徐
 * @since 2023/9/25 17:58
 */
public class ChartUtils {

    public static final char UNDERLINE = '_';

    private ChartUtils() {
        throw new IllegalAccessError();
    }

    /**
     * 驼峰转下划线
     *
     * @param param    源数据
     * @param charType 大小写区分 1-小写;2-大写;
     * @return String
     */
    public static String camelToUnderline(String param, Integer charType) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append(UNDERLINE);
            }
            // 统一都转大写
            if (charType == 2) {

                sb.append(Character.toUpperCase(c));
            }
            // 统一都转小写
            else {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param param 入参
     * @return String
     */
    public static String underlineToCamel(String param) {
        if (StringUtils.isBlank(param)) {
            return "";
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        // "_" 后转大写标志,默认字符前面没有"_"
        boolean flag = false;
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (c == UNDERLINE) {
                flag = true;
                // 标志设置为true,跳过
            } else {
                if (flag) {
                    // 表示当前字符前面是"_" ,当前字符转大写
                    sb.append(Character.toUpperCase(param.charAt(i)));
                    // 重置标识
                    flag = false;
                } else {
                    sb.append(Character.toLowerCase(param.charAt(i)));
                }
            }
        }
        return sb.toString();
    }
}
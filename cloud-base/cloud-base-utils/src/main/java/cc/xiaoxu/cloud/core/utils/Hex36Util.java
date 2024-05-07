package cc.xiaoxu.cloud.core.utils;

public class Hex36Util {

    /**
     * 将十进制数转换为36进制字符串
     */
    public static String convertToBase36(long decimal) {

        if (decimal == 0) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        while (decimal > 0) {
            long remainder = decimal % 36;
            // 将余数转换为对应的字母或数字
            char ch = (char) (remainder <= 9 ? '0' + remainder : 'A' + (remainder - 10));
            result.insert(0, ch);
            decimal /= 36;
        }
        return result.toString();
    }
}
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

    /**
     * 将36进制字符串转换为十进制数
     */
    public static int convertFromBase36(String base36) {
        int decimal = 0;
        int length = base36.length();
        for (int i = 0; i < length; i++) {
            char ch = base36.charAt(length - i - 1);
            int digit = getDigitFromChar(ch);
            decimal += digit * (int) Math.pow(36, i);
        }
        return decimal;
    }

    /**
     * 将单个字符转换为0-35的数值
     */
    private static int getDigitFromChar(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        if (ch >= 'A' && ch <= 'Z') {
            return ch - 'A' + 10;
        }
        // 如果是小写字母，也需要处理
        if (ch >= 'a' && ch <= 'z') {
            return ch - 'a' + 10;
        }
        throw new IllegalArgumentException("无效的36进制数字: " + ch);
    }
}
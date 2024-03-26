package cc.xiaoxu.cloud.core.utils.text;

import java.io.UnsupportedEncodingException;

/**
 * 汉字处理工具类
 * <p>
 * 2022.01.14 下午 6:43
 *
 * @author XiaoXu
 */
public final class ChineseUtils {

    private final static int[] LI_SEC_POS_VALUE = {1601, 1637, 1833, 2078, 2274,
            2302, 2433, 2594, 2787, 3106, 3212, 3472, 3635, 3722, 3730, 3858,
            4027, 4086, 4390, 4558, 4684, 4925, 5249, 5590};
    private final static String[] LC_FIRST_LETTER = {"a", "b", "c", "d", "e",
            "f", "g", "h", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "w", "x", "y", "z"};

    private ChineseUtils() {
    }

    /**
     * 取得给定汉字串的首字母串,即声母串
     *
     * @param str 给定汉字串
     * @return 声母串
     */
    public static String getAllFirstLetter(String str) {

        if (str == null || str.trim().length() == 0) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            stringBuilder.append(getFirstLetter(str.substring(i, i + 1)));
        }

        return stringBuilder.toString();
    }

    /**
     * 取得给定汉字的首字母,即声母
     *
     * @param chinese 给定的汉字
     * @return 给定汉字的声母
     */
    public static String getFirstLetter(String chinese) {

        if (chinese == null || chinese.trim().length() == 0) {
            return "";
        }
        chinese = conversionStr(chinese, "GB2312", "ISO8859-1");

        // 判断是不是汉字
        if (chinese.length() > 1) {
            // 汉字区码
            int liSectorCode = chinese.charAt(0);
            // 汉字位码
            int liPositionCode = chinese.charAt(1);
            liSectorCode = liSectorCode - 160;
            liPositionCode = liPositionCode - 160;
            // 汉字区位码
            int liSecPosCode = liSectorCode * 100 + liPositionCode;
            if (liSecPosCode > 1600 && liSecPosCode < 5590) {
                for (int i = 0; i < 23; i++) {
                    if (liSecPosCode >= LI_SEC_POS_VALUE[i]
                            && liSecPosCode < LI_SEC_POS_VALUE[i + 1]) {
                        chinese = LC_FIRST_LETTER[i];
                        break;
                    }
                }
            } else {
                // 非汉字字符,如图形符号或ASCII码
                chinese = conversionStr(chinese, "ISO8859-1", "GB2312");
                chinese = chinese.substring(0, 1);
            }
        }
        return chinese;
    }

    /**
     * 字符串编码转换
     *
     * @param str           要转换编码的字符串
     * @param charsetName   原来的编码
     * @param toCharsetName 转换后的编码
     * @return 经过编码转换后的字符串
     */
    private static String conversionStr(String str, String charsetName, String toCharsetName) {

        try {
            str = new String(str.getBytes(charsetName), toCharsetName);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("字符串编码转换异常：" + ex.getMessage());
        }
        return str;
    }

    /**
     * 数字转字母 1-26 ： A-Z
     */
    public static String numberToLetter(int num) {

        if (num <= 0) {
            return null;
        }
        StringBuilder letter = new StringBuilder();
        num--;
        do {
            if (letter.length() > 0) {
                num--;
            }
            letter.insert(0, ((char) (num % 26 + (int) 'A')));
            num = (num - num % 26) / 26;
        } while (num > 0);

        return letter.toString();
    }

    /**
     * 字母转数字  A-Z ：1-26
     */
    public static int letterToNumber(String letter) {

        int length = letter.length();
        int num;
        int number = 0;
        for (int i = 0; i < length; i++) {
            char ch = letter.charAt(length - i - 1);
            num = ch - 'A' + 1;
            num *= Math.pow(26, i);
            number += num;
        }
        return number;
    }
}
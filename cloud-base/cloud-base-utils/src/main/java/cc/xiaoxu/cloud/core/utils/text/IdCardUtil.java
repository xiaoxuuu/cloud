package cc.xiaoxu.cloud.core.utils.text;

import java.util.regex.Pattern;

/**
 * <p>类说明:身份证合法性校验</p>
 *
 * <p>18 位身份证号码：第 7、8、9、10 位为出生年份(四位数)，第 11、12 位为出生月份，第 13、14 位代表出生日期，第 17 位代表性别，奇数为男，偶数为女。</p>
 */
@SuppressWarnings({"all"})
public class IdCardUtil {

    /**
     * 禁止实例化
     */
    private IdCardUtil() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * <p>省，直辖市代码表</p>
     */
    private static String codeAndCity[][] = {{"11", "北京"}, {"12", "天津"},
            {"13", "河北"}, {"14", "山西"}, {"15", "内蒙古"}, {"21", "辽宁"},
            {"22", "吉林"}, {"23", "黑龙江"}, {"31", "上海"}, {"32", "江苏"},
            {"33", "浙江"}, {"34", "安徽"}, {"35", "福建"}, {"36", "江西"},
            {"37", "山东"}, {"41", "河南"}, {"42", "湖北"}, {"43", "湖南"},
            {"44", "广东"}, {"45", "广西"}, {"46", "海南"}, {"50", "重庆"},
            {"51", "四川"}, {"52", "贵州"}, {"53", "云南"}, {"54", "西藏"},
            {"61", "陕西"}, {"62", "甘肃"}, {"63", "青海"}, {"64", "宁夏"},
            {"65", "新疆"}, {"71", "台湾"}, {"81", "香港"}, {"82", "澳门"},
            {"91", "国外"}};

    /**
     * <p>市代码表</p>
     */
    private static String cityCode[] = {"11", "12", "13", "14", "15", "21", "22",
            "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
            "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63",
            "64", "65", "71", "81", "82", "91"};

    /**
     * 每位加权因子
     */
    private static int power[] = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 第 18 位校检码
     */
    private static String verifyCode[] = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};

    /**
     * <p>判断 18 位身份证的合法性</p>
     * <p>根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。</p>
     * <p>排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。</p>
     * <p>顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配给女性。</p>
     * <p>1. 前 1、2 位数字表示：所在省份的代码；</p>
     * <p>2. 第 3、4 位数字表示：所在城市的代码；</p>
     * <p>3. 第 5、6 位数字表示：所在区县的代码；</p>
     * <p>4. 第 7 ~ 14 位数字表示：出生年、月、日；</p>
     * <p>5. 第 15、16 位数字表示：所在地的派出所的代码；</p>
     * <p>6. 第 17 位数字表示性别：奇数表示男性，偶数表示女性；</p>
     * <p>7. 第 18 位数字是校检码：也有的说是个人信息码，一般是随计算机的随机产生，用来检验身份证的正确性。校检码可以是 0 ~ 9 的数字，有时也用 Ⅹ 表示。</p>
     * <p>第 18 位数字(校验码)的计算方法为： </p>
     * <p>1. 将前面的身份证号码 17 位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2</p>
     * <p>2. 将这 17 位数字和系数相乘的结果相加。</p>
     * <p>3. 用加出来和除以 11，看余数是多少？</p>
     * <p>4. 余数只可能有 0 1 2 3 4 5 6 7 8 9 10 这 11 个数字。其分别对应的最后一位身份证的号码为 1 0 X 9 8 7 6 5 4 3 2</p>
     * <p>5. 通过上面得知如果余数是 2，就会在身份证的第 18 位数字上出现罗马数字的 Ⅹ。如果余数是 10，身份证的最后一位号码就是 2</p>
     *
     * @param idCard 18 位身份证号
     * @return 成功返回 true
     */
    public static boolean validate(String idCard) {

        if (!isIdCard(idCard)) {
            return false;
        }
        // 获取前 17 位
        String idcard17 = idCard.substring(0, 17);
        // 获取第 18 位
        String idcard18Code = idCard.substring(17, 18);
        char c[] = null;
        String checkCode = "";
        // 是否都为数字
        if (isDigital(idcard17)) {
            c = idcard17.toCharArray();
        } else {
            return false;
        }

        if (null != c) {
            int bit[] = new int[idcard17.length()];

            bit = converCharToInt(c);

            int sum17 = 0;

            sum17 = getPowerSum(bit);

            // 将和值与 11 取模得到余数进行校验码判断
            checkCode = getCheckCodeBySum(sum17);
            if (null == checkCode) {
                return false;
            }
            // 将身份证的第 18 位与算出来的校码进行匹配，不相等就为假
            if (!idcard18Code.equalsIgnoreCase(checkCode)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 18 位身份证号码的基本数字和位数验校
     *
     * @param idCard 校验是否是 18 位身份证
     * @return 成功返回 true
     */
    public static boolean isIdCard(String idCard) {
        return Pattern.matches("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$", idCard);
    }

    /**
     * 数字验证
     *
     * @param str 前置校验
     * @return 通过返回 true
     */
    public static boolean isDigital(String str) {
        return str != null && !"".equals(str) && str.matches("^[0-9]*$");
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param bit 身份证数组
     * @return 值
     */
    public static int getPowerSum(int[] bit) {

        int sum = 0;

        if (power.length != bit.length) {
            return sum;
        }

        for (int i = 0; i < bit.length; i++) {
            for (int j = 0; j < power.length; j++) {
                if (i == j) {
                    sum = sum + bit[i] * power[j];
                }
            }
        }
        return sum;
    }

    /**
     * 获取值与 11 取模得到余数校验码
     *
     * @param sum17 余数
     * @return 校验码
     */
    public static String getCheckCodeBySum(int sum17) {
        switch (sum17 % 11) {
            case 10:
                return "2";
            case 9:
                return "3";
            case 8:
                return "4";
            case 7:
                return "5";
            case 6:
                return "6";
            case 5:
                return "7";
            case 4:
                return "8";
            case 3:
                return "9";
            case 2:
                return "x";
            case 1:
                return "0";
            case 0:
                return "1";
        }
        return null;
    }

    /**
     * 将字符数组转为整型数组
     *
     * @param c 数组
     * @return 整型结果
     * @throws NumberFormatException 转换异常
     */
    public static int[] converCharToInt(char[] c) throws NumberFormatException {
        int[] a = new int[c.length];
        int k = 0;
        for (char temp : c) {
            a[k++] = Integer.parseInt(String.valueOf(temp));
        }
        return a;
    }
}
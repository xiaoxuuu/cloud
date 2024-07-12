package cc.xiaoxu.cloud.core.utils.random.person.randomizer;

import cc.xiaoxu.cloud.core.utils.random.person.control.EmailControl;
import cc.xiaoxu.cloud.core.utils.random.person.control.PersonControl;

import java.util.Random;

/**
 * <p>邮箱处理器</p>
 *
 * @author 小徐
 * @since 2024/7/7 下午3:37
 */
public class EmailRandomizer {

    private static final String[] email_suffix = "@gmail.com,@yahoo.com,@msn.com,@hotmail.com,@aol.com,@ask.com,@live.com,@qq.com,@0355.net,@163.com,@163.net,@263.net,@3721.net,@yeah.net,@googlemail.com,@126.com,@sina.com,@sohu.com,@yahoo.com.cn".split(",");
    public static String base = "abcdefghijklmnopqrstuvwxyz0123456789._";

    public static String get() {

        return email(EmailControl.of(), new Random(System.currentTimeMillis()));
    }

    public static String get(PersonControl personControl) {

        return email(personControl.getEmail(), new Random(System.currentTimeMillis()));
    }

    public static String get(PersonControl personControl, Random random) {

        return email(personControl.getEmail(), random);
    }

    private static String email(EmailControl email, Random random) {
        return email(email.getMin(), email.getMax(), random);
    }

    /**
     * 返回 Email
     *
     * @param lMin 最小长度
     * @param lMax 最大长度
     */
    public static String email(int lMin, int lMax, Random random) {
        int length = getNum(lMin, lMax, random);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = (int) (Math.random() * base.length());
            sb.append(base.charAt(number));
        }
        sb.append(email_suffix[(int) (Math.random() * email_suffix.length)]);
        return sb.toString();
    }

    public static int getNum(int start, int end, Random random) {
        return random.nextInt(end - start + 1) + start;
    }
}
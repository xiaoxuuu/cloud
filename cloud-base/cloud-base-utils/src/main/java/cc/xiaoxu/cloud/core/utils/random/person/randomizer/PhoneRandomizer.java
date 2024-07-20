package cc.xiaoxu.cloud.core.utils.random.person.randomizer;

import cc.xiaoxu.cloud.core.utils.random.person.control.PhoneControl;
import cc.xiaoxu.cloud.core.utils.random.person.enums.PhoneOperator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 手机号处理器
 */
public class PhoneRandomizer {

    /**
     * 中国广电
     */
    private static final Integer[] CHINA_BROADNET = {192};

    /**
     * 中国电信
     */
    private static final Integer[] CHINA_TELECOM = {170, 199, 193, 191, 190, 189, 181, 180, 177, 173, 153, 149, 141, 133};

    /**
     * 中国移动
     */
    private static final Integer[] CHINA_MOBILE = {170, 187, 188, 184, 183, 182, 137, 138, 178, 139, 136, 135, 172, 134, 195, 159, 158, 157, 197, 198, 144, 152, 151, 150, 148, 147};

    /**
     * 中国联通
     */
    private static final Integer[] CHINA_UNICOM = {131, 132, 196, 146, 186, 185, 176, 175, 171, 170, 130, 167, 166, 156, 155, 145};

    public static String get() {

        return get(PhoneControl.of(), new Random());
    }

    public static String get(PhoneControl phoneControl) {

        return get(phoneControl, new Random());
    }

    public static String get(PhoneControl phoneControl, Random random) {

        Set<PhoneOperator> phoneOperatorSet = Arrays.stream(phoneControl.getPhoneOperatorArray()).collect(Collectors.toSet());
        List<Integer> prefixList = new ArrayList<>();
        if (phoneOperatorSet.contains(PhoneOperator.RANDOM)) {
            prefixList.addAll(Arrays.asList(CHINA_BROADNET));
            prefixList.addAll(Arrays.asList(CHINA_TELECOM));
            prefixList.addAll(Arrays.asList(CHINA_MOBILE));
            prefixList.addAll(Arrays.asList(CHINA_UNICOM));
        } else {
            for (PhoneOperator phoneOperator : phoneControl.getPhoneOperatorArray()) {
                switch (phoneOperator) {
                    case CHINA_BROADNET -> prefixList.addAll(Arrays.asList(CHINA_BROADNET));
                    case CHINA_TELECOM -> prefixList.addAll(Arrays.asList(CHINA_TELECOM));
                    case CHINA_MOBILE -> prefixList.addAll(Arrays.asList(CHINA_MOBILE));
                    case CHINA_UNICOM -> prefixList.addAll(Arrays.asList(CHINA_UNICOM));
                }
            }
        }

        Integer prefix = prefixList.get(random.nextInt(prefixList.size()));
        random.nextInt(99999999);
        String formattedNumber = String.format("%08d", random.nextInt(99999999));
        return prefix + formattedNumber;
    }
}
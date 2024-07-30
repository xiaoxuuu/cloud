package cc.xiaoxu.cloud.core.utils.random.person.randomizer;

import cc.xiaoxu.cloud.core.utils.random.person.control.GenderControl;
import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 性别处理器
 */
public class GenderRandomizer {

    public static Gender get() {

        return get(GenderControl.of(), ThreadLocalRandom.current());
    }

    public static Gender get(GenderControl genderControl) {

        return get(genderControl, ThreadLocalRandom.current());
    }

    public static Gender get(GenderControl genderControl, ThreadLocalRandom threadLocalRandom) {

        if (genderControl.getGender() != Gender.RANDOM) {
            return genderControl.getGender();
        }
        return threadLocalRandom.nextBoolean() ? Gender.WOMAN : Gender.MAN;
    }
}
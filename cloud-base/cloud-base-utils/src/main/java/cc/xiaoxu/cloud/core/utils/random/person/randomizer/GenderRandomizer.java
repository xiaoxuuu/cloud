package cc.xiaoxu.cloud.core.utils.random.person.randomizer;

import cc.xiaoxu.cloud.core.utils.random.person.control.GenderControl;
import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;

import java.util.Random;

/**
 * 性别处理器
 */
public class GenderRandomizer {

    public static Gender get() {

        return get(GenderControl.of(), new Random());
    }

    public static Gender get(GenderControl genderControl) {

        return get(genderControl, new Random());
    }

    public static Gender get(GenderControl genderControl, Random random) {

        if (genderControl.getGender() != Gender.RANDOM) {
            return genderControl.getGender();
        }
        return random.nextBoolean() ? Gender.WOMAN : Gender.MAN;
    }
}
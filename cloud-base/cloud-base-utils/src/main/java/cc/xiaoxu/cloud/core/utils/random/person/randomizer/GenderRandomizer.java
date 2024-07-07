package cc.xiaoxu.cloud.core.utils.random.person.randomizer;

import cc.xiaoxu.cloud.core.utils.random.person.PersonControl;
import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;

import java.util.Random;

/**
 * 性别处理器
 */
public class GenderRandomizer {

    /**
     * 处理性别
     */
    public static Gender get(PersonControl personControl, Random random) {

        if (personControl.getGender() != Gender.RANDOM) {
            return personControl.getGender();
        }
        return random.nextBoolean() ? Gender.WOMAN : Gender.MAN;
    }
}
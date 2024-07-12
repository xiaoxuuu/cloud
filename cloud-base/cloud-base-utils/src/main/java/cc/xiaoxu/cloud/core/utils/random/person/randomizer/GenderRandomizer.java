package cc.xiaoxu.cloud.core.utils.random.person.randomizer;

import cc.xiaoxu.cloud.core.utils.random.person.control.PersonControl;
import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;

import java.util.Random;

/**
 * 性别处理器
 */
public class GenderRandomizer {

    public static Gender get() {

        return get(PersonControl.of(), new Random());
    }

    public static Gender get(PersonControl personControl) {

        return get(personControl, new Random());
    }

    public static Gender get(PersonControl personControl, Random random) {

        if (personControl.getGender().getGender() != Gender.RANDOM) {
            return personControl.getGender().getGender();
        }
        return random.nextBoolean() ? Gender.WOMAN : Gender.MAN;
    }
}
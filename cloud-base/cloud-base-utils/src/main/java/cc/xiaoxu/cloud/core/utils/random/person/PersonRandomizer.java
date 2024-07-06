package cc.xiaoxu.cloud.core.utils.random.person;

import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;

import java.util.Random;

public class PersonRandomizer {

    /**
     * 处理性别
     */
    public static void buildGender(Person person, PersonControl personControl, Random random) {
        if (personControl.getGender() == Gender.RANDOM) {
            person.setGender(random.nextBoolean() ? Gender.WOMAN : Gender.MAN);
        } else {
            person.setGender(personControl.getGender());
        }
    }
}
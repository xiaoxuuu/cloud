package cc.xiaoxu.cloud.my.demo;

import cc.xiaoxu.cloud.core.utils.random.person.Person;
import cc.xiaoxu.cloud.core.utils.random.person.PersonInitializer;
import cc.xiaoxu.cloud.core.utils.random.person.control.*;
import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;
import cc.xiaoxu.cloud.core.utils.random.person.enums.PhoneOperator;

import java.util.List;

public class DemoFakePerson {

    public static void main(String[] args) {

        PersonControl control = PersonControl.of()
                .name(NameControl.of().surname(false).doubleSurname(true).desensitizationLength(2))
                .gender(GenderControl.of().gender(Gender.RANDOM))
                .phone(PhoneControl.of().operator(PhoneOperator.RANDOM))
                .email(EmailControl.of().min(1).max(2).onlyLetter(true))
                .check();
        List<Person> some = PersonInitializer.of().control(control).getSome(100);
        List<Person> random = PersonInitializer.of().getSome(100);
        System.out.println(some);
        System.out.println(random);
    }
}
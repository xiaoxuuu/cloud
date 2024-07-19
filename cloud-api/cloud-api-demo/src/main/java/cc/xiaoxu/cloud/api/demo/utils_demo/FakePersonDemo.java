package cc.xiaoxu.cloud.api.demo.utils_demo;

import cc.xiaoxu.cloud.core.utils.random.person.Person;
import cc.xiaoxu.cloud.core.utils.random.person.PersonInitializer;
import cc.xiaoxu.cloud.core.utils.random.person.control.EmailControl;
import cc.xiaoxu.cloud.core.utils.random.person.control.GenderControl;
import cc.xiaoxu.cloud.core.utils.random.person.control.NameControl;
import cc.xiaoxu.cloud.core.utils.random.person.control.PersonControl;
import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;

import java.util.List;

public class FakePersonDemo {

    public static void main(String[] args) {

        PersonControl control = PersonControl.of()
                .name(NameControl.of().useSurname(false).useDoubleSurname(true).desensitizationLength(2))
                .gender(GenderControl.of().gender(Gender.RANDOM))
                .email(EmailControl.of().min(1).max(2).onlyLetter(true))
                .check();
        List<Person> some = PersonInitializer.of().control(control).getSome(100);
        List<Person> random = PersonInitializer.of().getSome(100);
        System.out.println();
    }
}
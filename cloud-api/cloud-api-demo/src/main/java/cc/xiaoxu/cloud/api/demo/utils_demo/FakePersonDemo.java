package cc.xiaoxu.cloud.api.demo.utils_demo;

import cc.xiaoxu.cloud.core.utils.random.person.PersonInitializer;

import java.util.List;

public class FakePersonDemo {

    public static void main(String[] args) {

        PersonInitializer.Control control = PersonInitializer.Control.of().gender(PersonInitializer.Gender.MAN);
        List<PersonInitializer.Person> some = PersonInitializer.of().control(control).getSome(100);
        List<PersonInitializer.Person> random = PersonInitializer.of().getSome(100);
        System.out.println();
    }
}
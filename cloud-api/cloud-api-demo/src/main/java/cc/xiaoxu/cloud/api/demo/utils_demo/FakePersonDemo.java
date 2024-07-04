package cc.xiaoxu.cloud.api.demo.utils_demo;

import cc.xiaoxu.cloud.core.utils.random.person.FakePerson;

import java.util.List;

public class FakePersonDemo {

    public static void main(String[] args) {

        FakePerson.Control control = FakePerson.Control.of().gender(FakePerson.Gender.MAN);
        List<FakePerson.Person> some = FakePerson.of().control(control).getSome(100);
        List<FakePerson.Person> random = FakePerson.of().getSome(100);
        System.out.println();
    }
}
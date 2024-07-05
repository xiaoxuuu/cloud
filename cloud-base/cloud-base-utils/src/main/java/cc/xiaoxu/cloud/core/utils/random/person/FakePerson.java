package cc.xiaoxu.cloud.core.utils.random.person;

import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * <p>虚拟人信息</p>
 *
 * @author 小徐
 * @since 2024/7/1 下午2:46
 */
public class FakePerson {

    /**
     * 属性控制
     */
    private PersonControl personControl;

    /**
     * 随机工具
     */
    private final Random random = new Random(System.currentTimeMillis());

    /**
     * 私有构造，请使用 FakePerson.of 开始
     */
    private FakePerson() {
        this.personControl = new PersonControl();
    }


    /**
     * 启动
     * @return FakePerson
     */
    public static FakePerson of() {
        return new FakePerson();
    }

    /**
     * 可控随机
     * @param personControl 属性控制器
     */
    public FakePerson control(PersonControl personControl) {
        this.personControl = personControl;
        return this;
    }

    /**
     * 获取一个
     * @return 人
     */
    public Person getOne() {

        return getOne(1);
    }

    /**
     * 获取一个
     * @return 人
     */
    public Person getOne(Integer id) {

        Person person = new Person();
        person.setId(id);
        buildGender(person);
        return person;
    }

    /**
     * 获取多个
     * @param i 数量
     * @return 人
     */
    public List<Person> getSome(int i) {

        return Stream.iterate(1, n -> n + 1)
                .limit(i)
                .map(this::getOne)
                .toList();
    }

    /**
     * 处理性别
     */
    private void buildGender(Person person) {
        if (personControl.getGender() == Gender.RANDOM) {
            person.setGender(random.nextBoolean() ? Gender.WOMAN : Gender.MAN);
        } else {
            person.setGender(personControl.getGender());
        }
    }
}
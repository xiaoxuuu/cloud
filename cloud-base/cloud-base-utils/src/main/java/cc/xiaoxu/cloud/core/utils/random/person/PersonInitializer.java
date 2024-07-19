package cc.xiaoxu.cloud.core.utils.random.person;

import cc.xiaoxu.cloud.core.utils.random.person.control.PersonControl;
import cc.xiaoxu.cloud.core.utils.random.person.randomizer.EmailRandomizer;
import cc.xiaoxu.cloud.core.utils.random.person.randomizer.GenderRandomizer;
import cc.xiaoxu.cloud.core.utils.random.person.randomizer.NameRandomizer;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

/**
 * <p>虚拟人信息</p>
 *
 * @author 小徐
 * @since 2024/7/1 下午2:46
 */
public class PersonInitializer {

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
    private PersonInitializer() {
        this.personControl = new PersonControl();
    }

    /**
     * 启动
     * @return FakePerson
     */
    public static PersonInitializer of() {
        return new PersonInitializer();
    }

    /**
     * 可控随机
     * @param personControl 属性控制器
     */
    public PersonInitializer control(PersonControl personControl) {
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
        person.setName(NameRandomizer.get(personControl.getNameControl()));
        person.setGender(GenderRandomizer.get(personControl.getGenderControl(), random));
        person.setEmail(EmailRandomizer.get(personControl.getEmailControl(), random));
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
}
package cc.xiaoxu.cloud.core.utils.random;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private final Control control;

    /**
     * 随机工具
     */
    private final Random random = new Random(System.currentTimeMillis());

    /**
     * 完全随机
     */
    public FakePerson() {
        this.control = new Control();
    }

    /**
     * 可控随机
     * @param control 属性控制器
     */
    public FakePerson(Control control) {
        this.control = control;
    }

    /**
     * 获取一个
     * @return 人
     */
    public Person getOne() {

        Person person = new Person();
        buildGender(person);
        return person;
    }

    /**
     * 获取多个
     * @param i 数量
     * @return 人
     */
    public List<Person> getSome(int i) {

        List<Person> list = new ArrayList<>(i);
        for (int j = 0; j < i; j++) {
            list.add(getOne());
        }
        return list;
    }

    /**
     * 处理性别
     * @param person
     */
    private void buildGender(Person person) {
        if (control.gender == 0) {
            person.gender = random.nextBoolean() ? Person.Gender.WOMAN : Person.Gender.MAN;
        } else {
            person.gender = control.gender == 1 ? Person.Gender.WOMAN : Person.Gender.MAN;
        }
    }

    /**
     * 输出控制
     */
    public static class Person {

        /**
         * 性别：
         */
        private Gender gender;

        @AllArgsConstructor
        public enum Gender {
            MAN,
            WOMAN
        }
    }

    /**
     * 输出控制
     */
    public static class Control {

        /**
         * 性别：未指定 0、女性 1、男 2
         */
        private Integer gender;

        public Control() {
            this.gender = 0;
        }
    }
}
package cc.xiaoxu.cloud.core.utils.random;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

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

        return IntStream.of(i).boxed().map(this::getOne).toList();
    }

    /**
     * 处理性别
     */
    private void buildGender(Person person) {
        if (control.gender == Gender.RANDOM) {
            person.gender = random.nextBoolean() ? Gender.WOMAN : Gender.MAN;
        } else {
            person.gender = control.gender;
        }
    }

    /**
     * 结果
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Person {

        /**
         * id
         */
        private Integer id;

        /**
         * 性别
         */
        private Gender gender;
    }

    /**
     * 输出控制
     */
    public static class Control {

        /**
         * 性别
         */
        private Gender gender;

        /**
         * 初始化
         */
        private Control() {
            this.gender = Gender.RANDOM;
        }

        /**
         * 启动
         * @return Control
         */
        public Control of() {
            return new Control();
        }

        /**
         * 性别
         * @param gender 性别
         * @return Control
         */
        public Control gender(Gender gender) {
            this.gender = gender;
            return this;
        }
    }

    @AllArgsConstructor
    public enum Gender {
        RANDOM,
        MAN,
        WOMAN,
    }
}
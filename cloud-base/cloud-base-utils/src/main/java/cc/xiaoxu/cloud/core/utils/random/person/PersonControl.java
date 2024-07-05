package cc.xiaoxu.cloud.core.utils.random.person;

import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PersonControl {

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 初始化
     */
    public PersonControl() {
        this.gender = Gender.RANDOM;
    }

    /**
     * 启动
     * @return Control
     */
    public static PersonControl of() {
        return new PersonControl();
    }

    /**
     * 性别
     * @param gender 性别
     * @return Control
     */
    public PersonControl gender(Gender gender) {
        this.gender = gender;
        return this;
    }
}
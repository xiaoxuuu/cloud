package cc.xiaoxu.cloud.core.utils.random.person;

import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Control {

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 初始化
     */
    public Control() {
        this.gender = Gender.RANDOM;
    }

    /**
     * 启动
     * @return Control
     */
    public static Control of() {
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
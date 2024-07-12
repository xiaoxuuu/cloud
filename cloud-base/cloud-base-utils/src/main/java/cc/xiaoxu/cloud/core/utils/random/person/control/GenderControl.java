package cc.xiaoxu.cloud.core.utils.random.person.control;

import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenderControl implements ControlInterface {

    /**
     * 性别
     */
    private Gender gender;

    private GenderControl() {
        this.gender = Gender.RANDOM;
    }

    public static GenderControl of() {
        return new GenderControl();
    }

    public GenderControl gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    @Override
    public String check() {

        if (null == gender) {
            return "性别不能为空";
        }
        return null;
    }
}
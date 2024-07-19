package cc.xiaoxu.cloud.core.utils.random.person.control;

import cc.xiaoxu.cloud.core.utils.ConditionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class PersonControl {

    /**
     * 姓名
     */
    private NameControl nameControl;

    /**
     * 性别
     */
    private GenderControl genderControl;

    /**
     * 邮箱
     */
    private EmailControl emailControl;

    /**
     * 初始化
     */
    public PersonControl() {
        this.nameControl = NameControl.of();
        this.genderControl = GenderControl.of();
        this.emailControl = EmailControl.of();
    }

    /**
     * 启动
     * @return Control
     */
    public static PersonControl of() {
        return new PersonControl();
    }

    /**
     * 姓名
     * @param name 姓名
     * @return Control
     */
    public PersonControl name(NameControl name) {
        this.nameControl = name;
        return this;
    }

    /**
     * 性别
     * @param gender 性别
     * @return Control
     */
    public PersonControl gender(GenderControl gender) {
        this.genderControl = gender;
        return this;
    }

    /**
     * 邮箱
     * @param email 邮箱
     * @return Control
     */
    public PersonControl email(EmailControl email) {
        this.emailControl = email;
        return this;
    }

    public PersonControl check() {

        ConditionUtils.of(nameControl.check(), StringUtils::isNotBlank).orThrow();
        ConditionUtils.of(genderControl.check(), StringUtils::isNotBlank).orThrow();
        ConditionUtils.of(emailControl.check(), StringUtils::isNotBlank).orThrow();
        return this;
    }
}
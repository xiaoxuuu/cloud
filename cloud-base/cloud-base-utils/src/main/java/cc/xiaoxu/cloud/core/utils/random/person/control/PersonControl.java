package cc.xiaoxu.cloud.core.utils.random.person.control;

import cc.xiaoxu.cloud.core.utils.ConditionUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
public class PersonControl {

    /**
     * 性别
     */
    private GenderControl gender;

    /**
     * 邮箱
     */
    private EmailControl email;

    /**
     * 初始化
     */
    public PersonControl() {
        this.gender = GenderControl.of();
        this.email = EmailControl.of();
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
    public PersonControl gender(GenderControl gender) {
        this.gender = gender;
        return this;
    }

    /**
     * 邮箱
     * @param email 邮箱
     * @return Control
     */
    public PersonControl email(EmailControl email) {
        this.email = email;
        return this;
    }

    public PersonControl check() {

        ConditionUtils.of(email.check(), StringUtils::isNotBlank).orThrow();
        return this;
    }
}
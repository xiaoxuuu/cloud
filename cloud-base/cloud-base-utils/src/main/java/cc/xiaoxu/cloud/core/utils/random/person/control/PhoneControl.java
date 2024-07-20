package cc.xiaoxu.cloud.core.utils.random.person.control;

import cc.xiaoxu.cloud.core.utils.random.person.enums.PhoneOperator;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PhoneControl implements ControlInterface {

    /**
     * 运营商类型
     */
    private PhoneOperator[] phoneOperatorArray;

    private PhoneControl() {
        this.phoneOperatorArray = new PhoneOperator[]{PhoneOperator.RANDOM};
    }

    public static PhoneControl of() {
        return new PhoneControl();
    }

    public PhoneControl operator(PhoneOperator... phoneOperator) {
        this.phoneOperatorArray = phoneOperator;
        return this;
    }

    @Override
    public String check() {

        if (null == phoneOperatorArray || phoneOperatorArray.length == 0) {
            return "运营商类型不能为空";
        }
        return null;
    }
}
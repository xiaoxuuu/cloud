package cc.xiaoxu.cloud.core.utils.random.person.control;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailControl implements ControlInterface {

    /**
     * 最短
     */
    private Integer min;

    /**
     * 最长
     */
    private Integer max;

    // TODO 纯数字暂不支持

    /**
     * 纯数字
     */
    private Boolean onlyNumber;

    // TODO 纯数字暂不支持

    /**
     * 纯字母
     */
    private Boolean onlyLetter;

    private EmailControl() {
        this.min = 4;
        this.max = 10;
        this.onlyNumber = false;
        this.onlyLetter = false;
    }

    public static EmailControl of() {
        return new EmailControl();
    }

    public EmailControl min(Integer min) {
        this.min = min;
        return this;
    }

    public EmailControl max(Integer max) {
        this.max = max;
        return this;
    }

    public EmailControl onlyNumber(Boolean onlyNumber) {
        this.onlyNumber = onlyNumber;
        return this;
    }

    public EmailControl onlyLetter(Boolean onlyLetter) {
        this.onlyLetter = onlyLetter;
        return this;
    }

    @Override
    public String check() {

        if (min < 1) {
            return "邮箱长度最短不能低于 1";
        }
        if (max < 1) {
            return "邮箱长度最长不能低于 1";
        }
        if (max < min) {
            return "邮箱长度最长小于最短";
        }
        return null;
    }
}
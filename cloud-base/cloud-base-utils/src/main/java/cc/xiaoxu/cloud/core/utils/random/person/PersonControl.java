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
     * 邮箱
     */
    private Email email;

    /**
     * 初始化
     */
    public PersonControl() {
        this.gender = Gender.RANDOM;
        this.email = Email.of();
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

    /**
     * 邮箱
     * @param email 邮箱
     * @return Control
     */
    public PersonControl email(Email email) {
        this.email = email;
        return this;
    }

    @Data
    @AllArgsConstructor
    public static class Email {

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

        private Email() {
            this.min = 4;
            this.max = 10;
            this.onlyNumber = false;
            this.onlyLetter = false;
        }

        public static Email of() {
            return new Email();
        }

        public Email min(Integer min) {
            this.min = min;
            return this;
        }

        public Email max(Integer max) {
            this.max = max;
            return this;
        }

        public Email onlyNumber(Boolean onlyNumber) {
            this.onlyNumber = onlyNumber;
            return this;
        }

        public Email onlyLetter(Boolean onlyLetter) {
            this.onlyLetter = onlyLetter;
            return this;
        }
    }
}
package cc.xiaoxu.cloud.core.utils.random.person;

import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;
}
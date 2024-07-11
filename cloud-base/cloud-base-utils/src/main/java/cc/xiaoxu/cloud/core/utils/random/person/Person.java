package cc.xiaoxu.cloud.core.utils.random.person;

import cc.xiaoxu.cloud.core.utils.random.person.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    /**
     * id
     */
    private Integer id;

    /**
     * 性别
     */
    private Gender gender;

    /**
     * 邮箱
     */
    private String email;
}
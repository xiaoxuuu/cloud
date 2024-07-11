package cc.xiaoxu.cloud.core.utils.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;

/**
 * 枚举示例
 *
 * @author 小徐
 * @since 2022/12/9 13:46
 */
public enum DemoEnum implements EnumInterface<Long>, EnumDescInterface {

    /**
     * 示例枚举
     */
    ONE(1L, "数字 1"),
    TWO(2L, "数字 2"),
    ;

    /**
     * 编码
     */
    private final Long code;

    /**
     * 介绍
     */
    private final String introduction;

    DemoEnum(Long code, String introduction) {
        this.code = code;
        this.introduction = introduction;
    }

    @Override
    public Long getCode() {
        return code;
    }

    @Override
    public String getIntroduction() {
        return introduction;
    }

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
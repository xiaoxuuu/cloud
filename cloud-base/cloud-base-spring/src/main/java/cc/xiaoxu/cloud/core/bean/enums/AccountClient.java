package cc.xiaoxu.cloud.core.bean.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountClient implements EnumInterface<String> {

    /**
     * 账号客户端枚举
     */
    ADMIN("ADMIN", "管理端"),
    WEB("WEB", "WEB"),
    APP("APP", "APP"),
    WECHAT("WECHAT", "微信小程序"),
    ;

    private final String code;
    private final String introduction;
}
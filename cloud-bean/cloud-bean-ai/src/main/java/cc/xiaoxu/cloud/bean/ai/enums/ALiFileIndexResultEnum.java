package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ALiFileIndexResultEnum implements EnumInterface<String>, EnumDescInterface {

    PENDING("index_pending", "等待索引", true),
    RUNNING("index_running", "文件索引中", false),
    COMPLETED("index_completed", "文件索引完成", true),
    FAILED("index_error", "文件索引失败", true),
    ;

    private final String code;
    private final String introduction;
    private final Boolean end;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
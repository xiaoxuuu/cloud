package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KnowledgeTypeEnum implements EnumInterface<String>, EnumDescInterface {

    ALi_FILE("ali_file", "文件"),
    TABLE("table", "数据表"),
    CUSTOM("custom", "自定义"),
    ;

    private final String code;
    private final String introduction;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
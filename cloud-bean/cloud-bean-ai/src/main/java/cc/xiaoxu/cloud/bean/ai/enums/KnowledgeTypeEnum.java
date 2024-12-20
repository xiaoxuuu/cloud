package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KnowledgeTypeEnum implements EnumInterface<String>, EnumDescInterface {

    FILE_ALI("file_ali", "文件-阿里"),
    FILE_LOCAL("file_local", "文件-本地"),
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
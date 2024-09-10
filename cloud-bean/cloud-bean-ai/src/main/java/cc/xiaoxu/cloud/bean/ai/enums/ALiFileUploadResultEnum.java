package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ALiFileUploadResultEnum implements EnumInterface<String>, EnumDescInterface {

    INIT("upload_init", "等待上传", false),
    PARSING("upload_parsing", "上传文件中", false),
    PARSE_SUCCESS("upload_success", "上传文件完成", true),
    PARSE_FAILED("upload_failed", "上传文件失败", true),
    ;

    private final String code;
    private final String introduction;
    private final Boolean end;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
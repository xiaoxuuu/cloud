package cc.xiaoxu.cloud.bean.ai.enums;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumDescInterface;
import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileStatusEnum implements EnumInterface<String>, EnumDescInterface {

    /**
     * 文件上传
     */
    UPLOAD_INIT(ALiFileUploadResultEnum.INIT.getCode(), ALiFileUploadResultEnum.INIT.getIntroduction(), ALiFileUploadResultEnum.INIT.getEnd()),
    UPLOAD_PARSING(ALiFileUploadResultEnum.PARSING.getCode(), ALiFileUploadResultEnum.PARSING.getIntroduction(), ALiFileUploadResultEnum.PARSING.getEnd()),
    UPLOAD_PARSE_SUCCESS(ALiFileUploadResultEnum.PARSE_SUCCESS.getCode(), ALiFileUploadResultEnum.PARSE_SUCCESS.getIntroduction(), ALiFileUploadResultEnum.PARSE_SUCCESS.getEnd()),
    UPLOAD_PARSE_FAILED(ALiFileUploadResultEnum.PARSE_FAILED.getCode(), ALiFileUploadResultEnum.PARSE_FAILED.getIntroduction(), ALiFileUploadResultEnum.PARSE_FAILED.getEnd()),

    /**
     * 索引构建
     */
    INDEX_PENDING(ALiFileIndexResultEnum.PENDING.getCode(), ALiFileIndexResultEnum.PENDING.getIntroduction(), ALiFileIndexResultEnum.PENDING.getEnd()),
    INDEX_RUNNING(ALiFileIndexResultEnum.RUNNING.getCode(), ALiFileIndexResultEnum.RUNNING.getIntroduction(), ALiFileIndexResultEnum.RUNNING.getEnd()),
    INDEX_COMPLETED(ALiFileIndexResultEnum.COMPLETED.getCode(), ALiFileIndexResultEnum.COMPLETED.getIntroduction(), ALiFileIndexResultEnum.COMPLETED.getEnd()),
    INDEX_FAILED(ALiFileIndexResultEnum.FAILED.getCode(), ALiFileIndexResultEnum.FAILED.getIntroduction(), ALiFileIndexResultEnum.FAILED.getEnd()),

    /**
     * 读取文件切片
     */
    SECTION_READ("section_read", "读取文件切片中", false),

    /**
     * 向量计算
     */
    VECTOR_CALC("vector_calc", "向量计算中", false),

    /**
     * 数据准备就绪
     */
    ALL_COMPLETED("all_completed", "任务全部结束", true),
    ;

    private final String code;
    private final String introduction;
    private final Boolean end;

    @Override
    public String enhanceApiDesc() {
        return enhanceApiDesc(name(), introduction);
    }
}
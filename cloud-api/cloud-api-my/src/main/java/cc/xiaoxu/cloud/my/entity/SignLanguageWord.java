package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseInfoEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_sign_language_word")
@NoArgsConstructor
@AllArgsConstructor
public class SignLanguageWord extends BaseInfoEntity {

    @Schema(description = "词汇名称")
    private String wordName;

    @Schema(description = "词汇拼音")
    private String wordPinyin;

    @Schema(description = "词汇描述")
    private String explainText;

    @Schema(description = "所属词典")
    private String dictId;
}
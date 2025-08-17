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
@TableName("t_sign_language_word_rela")
@NoArgsConstructor
@AllArgsConstructor
public class SignLanguageWordRela extends BaseInfoEntity {

    @Schema(description = "词汇1")
    private Integer wordIdLeft;

    @Schema(description = "词汇2")
    private Integer wordIdRight;
}
package cc.xiaoxu.cloud.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class SignLanguageWordVO extends BaseIdVO {

    @Schema(description = "词汇名称")
    private String wordName;

    @Schema(description = "词汇拼音")
    private String wordPinyin;

    @Schema(description = "词汇描述")
    private String explainText;

    @Schema(description = "所属词典")
    private String dictId;

    @Schema(description = "词汇ID")
    private String wordId;

    @Schema(description = "关联ID")
    private Set<Integer> wordIdList;
}
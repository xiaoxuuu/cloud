package cc.xiaoxu.cloud.my.bean.es;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.Analyzer;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

@Data
@IndexName("website")
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsiteEs {

    @Schema(description = "es 中的唯一 id，对应 MySQL 中的 id")
    @IndexId(type = IdType.CUSTOMIZE)
    private String id;

    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD, ignoreCase = true)
    @Schema(description = "展示名称")
    private String shortName;

    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD, ignoreCase = true)
    @Schema(description = "网站名称（抓取）")
    private String websiteName;

    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD, ignoreCase = true)
    @Schema(description = "链接")
    private String url;

    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD, ignoreCase = true)
    @Schema(description = "网站描述")
    private String description;

    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD, ignoreCase = true)
    @Schema(description = "标签")
    private String label;

    @IndexField(fieldType = FieldType.TEXT, analyzer = Analyzer.IK_SMART, searchAnalyzer = Analyzer.IK_MAX_WORD, ignoreCase = true)
    @Schema(description = "类型")
    private String type;

    @Schema(description = "图标id")
    private String iconId;

    @Schema(description = "上次正常访问时间（抓取）")
    private String lastAvailableTime;

    @Schema(description = "访问次数")
    private Integer visitNum;

    @Schema(description = "排序")
    private String sort;

    @Schema(description = "描述")
    private String remark;
}
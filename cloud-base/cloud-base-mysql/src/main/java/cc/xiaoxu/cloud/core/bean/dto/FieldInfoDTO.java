package cc.xiaoxu.cloud.core.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "表结构信息")
public class FieldInfoDTO {

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "字段名")
    private String Field;

    @Schema(description = "字段类型")
    private String Type;

    @Schema(description = "是否为空")
    private String Null;

    @Schema(description = "主键")
    private String Key;

    @Schema(description = "默认数据")
    private String Default;

    @Schema(description = "附加信息")
    private String Extra;
}
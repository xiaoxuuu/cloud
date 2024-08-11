package cc.xiaoxu.cloud.bean.ai.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ALiSplitTxtPageVO {

    @Schema(description = "sk")
    private String apiKey;

    @Schema(description = "业务空间id")
    private String workspaceId;

    @Schema(description = "索引ID")
    private String indexId;

    @Schema(description = "文件ID")
    private String filed;

    @Schema(description = "页数")
    private Integer pageNum;

    @Schema(description = "每页文件大小")
    private Integer pageSize;
}
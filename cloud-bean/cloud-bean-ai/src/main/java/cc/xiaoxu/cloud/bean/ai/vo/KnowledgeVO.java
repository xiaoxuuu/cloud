package cc.xiaoxu.cloud.bean.ai.vo;

import cc.xiaoxu.cloud.bean.vo.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识")
public class KnowledgeVO extends BaseVO {

    @Schema(description = "资源类型：文件、数据表、自定义分类")
    private String type;

    @Schema(description = "资源类型名称")
    private String typeName;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "三方平台文件 id")
    private String threePartyFileId;

    @Schema(description = "三方平台附加信息")
    private String threePartyInfo;

    @Schema(description = "资源处理状态")
    private String status;

    @Schema(description = "资源处理状态名称")
    private String statusName;
}
package cc.xiaoxu.cloud.bean.dto.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德地图 - ID搜索")
public class AmapIdSearchRequestDTO {

    @Schema(description = "搜索 ID，支持 1-10 个", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> idList;

    @Schema(description = "显示字段控制，可选值：children,business,indoor,navi,photos")
    private List<String> showFieldList;
}
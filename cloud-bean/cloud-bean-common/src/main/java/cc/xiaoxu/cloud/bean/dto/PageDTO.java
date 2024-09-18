package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString
@Data
public class PageDTO {

    @Schema(description = "页码", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long current;

    @Schema(description = "每页数量", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;

    /**
     * 排序 传入排序字段以及正序倒序
     */
    @Schema(title = "排序")
    private List<OrderItemDTO> orders;
}
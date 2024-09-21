package cc.xiaoxu.cloud.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

    @Schema(description = "需要进行排序的字段")
    private String column;

    @Schema(description = "是否正序排列，默认 true")
    private boolean asc = true;

    public static List<OrderItemDTO> getDefaultSort() {
        return List.of(new OrderItemDTO("id", false));
    }
}
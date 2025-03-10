package cc.xiaoxu.cloud.core.bean.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseIdEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键id")
    private Integer id;
}
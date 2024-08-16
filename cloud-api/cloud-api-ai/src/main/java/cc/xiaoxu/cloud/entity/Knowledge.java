package cc.xiaoxu.cloud.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
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
@TableName("t_knowledge")
@NoArgsConstructor
@AllArgsConstructor
public class Knowledge extends BaseEntity {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "阿里文件id")
    private String fileId;
}
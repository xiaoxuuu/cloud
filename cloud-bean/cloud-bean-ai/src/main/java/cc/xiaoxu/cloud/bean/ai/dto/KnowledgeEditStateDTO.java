package cc.xiaoxu.cloud.bean.ai.dto;

import cc.xiaoxu.cloud.bean.dto.IdsDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.doc.annotation.SchemaEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeEditStateDTO extends IdsDTO {

    @Schema(description = "表名")
    @SchemaEnum(clazz = StateEnum.class)
    private String state;
}
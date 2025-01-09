package cc.xiaoxu.cloud.ai.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntityForPostgres;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_conversation_detail")
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetail extends BaseEntityForPostgres {

    @Schema(description = "所属对话id")
    private Integer conversationId;

    @Schema(description = "用户id")
    private String userId;

    @Schema(description = "模型内容id")
    private String detailId;

    @Schema(description = "")
    private String object;

    @Schema(description = "内容id")
    private Integer contentId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "模型")
    private String model;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "消耗token")
    private Integer token;
}
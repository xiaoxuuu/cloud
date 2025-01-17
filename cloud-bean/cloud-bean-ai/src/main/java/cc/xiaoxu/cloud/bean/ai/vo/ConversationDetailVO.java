package cc.xiaoxu.cloud.bean.ai.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDetailVO {

    @Schema(description = "所属对话id")
    private Integer conversationId;

    @Schema(description = "内容id")
    private Integer contentId;

    @Schema(description = "内容")
    private String content;

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
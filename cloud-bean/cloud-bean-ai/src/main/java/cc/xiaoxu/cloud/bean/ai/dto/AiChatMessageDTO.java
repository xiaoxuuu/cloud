package cc.xiaoxu.cloud.bean.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AiChatMessageDTO {

    private String role;

    private String content;
}
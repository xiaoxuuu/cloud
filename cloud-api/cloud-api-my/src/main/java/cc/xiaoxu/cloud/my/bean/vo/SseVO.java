package cc.xiaoxu.cloud.my.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SseVO<T> {

    private String type;

    private T data;
}
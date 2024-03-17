package cc.xiaoxu.cloud.api.demo.event_stream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonResult<T> {

    private String type;

    private T data;
}

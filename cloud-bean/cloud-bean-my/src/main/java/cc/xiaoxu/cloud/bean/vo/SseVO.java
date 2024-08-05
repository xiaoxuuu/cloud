package cc.xiaoxu.cloud.bean.vo;

import cc.xiaoxu.cloud.core.utils.enums.inter.EnumInterface;
import cc.xiaoxu.cloud.doc.annotation.SchemaEnum;
import com.baomidou.mybatisplus.annotation.EnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class SseVO<T> {

    @Schema(description = "sse类型")
    @SchemaEnum(clazz = Type.class)
    private Type type;

    private T data;

    private SseVO(Type type, T data) {
        this.type = type;
        this.data = data;
    }

    public static <T> SseVO<T> start() {
        return new SseVO<>(Type.START, null);
    }

    public static <T> SseVO<T> id(T t) {
        return new SseVO<>(Type.ID, t);
    }

    public static <T> SseVO<T> name(T t) {
        return new SseVO<>(Type.NAME, t);
    }

    public static <T> SseVO<T> msg(T t) {
        return new SseVO<>(Type.MSG, t);
    }

    public static <T> SseVO<T> end() {
        return new SseVO<>(Type.END, null);
    }

    @Getter
    @AllArgsConstructor
    public enum Type implements EnumInterface<String> {

        START("START", "会话开始"),
        ID("ID", "标记本次对话id"),
        NAME("NAME", "本次对话名称"),
        MSG("MSG", "会话内容"),
        END("END", "会话结束"),
        ;

        @EnumValue
        private final String code;
        private final String introduction;

    }
}
package cc.xiaoxu.cloud.core.bean.func;

import java.io.Serializable;

@FunctionalInterface
public interface FunctionGet<T, R> extends Serializable {
    R get(T source);
}
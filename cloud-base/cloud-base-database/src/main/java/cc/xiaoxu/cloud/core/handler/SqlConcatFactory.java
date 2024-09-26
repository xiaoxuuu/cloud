package cc.xiaoxu.cloud.core.handler;

import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SqlConcatFactory<T> {

    private static final Map<String, SqlConcatHandler<?>> services = new ConcurrentHashMap<>();

    public static SqlConcatHandler<?> get(String type) {
        return services.get(type);
    }

    public static SqlConcatHandler<?> get() {
        return services.values().iterator().next();
    }

    public static void register(String type, SqlConcatHandler<?> sqlConcatHandler) {

        Assert.notNull(type, "type can't be NULL");
        services.put(type, sqlConcatHandler);
    }
}
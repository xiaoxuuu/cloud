package cc.xiaoxu.cloud.core.constants;

public class RedisConstants {

    /**
     * 数据缓存 key
     */
    public static final String DATA_CACHE = "REDIS:DATA_CACHE:";

    /**
     * 统一缓存前缀
     */
    public static final String UNIFIED_CACHE = "REDIS:UNIFIED_CACHE:";

    /**
     * 是否使用统一缓存
     */
    public static final String ENABLE_UNIFIED_CACHE = "REDIS:ENABLE_UNIFIED_CACHE";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "REDIS:REPEAT_SUBMIT:";
}
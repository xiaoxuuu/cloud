package cc.xiaoxu.cloud.ai.constants;

/**
 * redis 监听的过期 key
 *
 * @author 小徐
 */
public interface RedisListenerConstants {

    /**
     * 异步任务 - 统一前缀
     */
    String PREFIX = "LIANLIAN:";

    /**
     * 文件上传检查 - 每日
     */
    String FILE_UPLOAD_RESULT_CHECK = PREFIX + "FILE_UPLOAD_RESULT_CHECK";

    /**
     * 文件上传检查 - 定时
     */
    String FILE_UPLOAD_RESULT_HANDLE = PREFIX + "FILE_UPLOAD_RESULT_HANDLE:";
}
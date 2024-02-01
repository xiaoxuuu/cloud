package cc.xiaoxu.cloud.core.cache.local;

import lombok.Data;

@Data
public class LocalCacheDTO {

    /**
     * 键
     */
    private String key;

    /**
     * 值
     */
    private Object value;

    /**
     * 过期时间
     */
    private Long expireTime;
}
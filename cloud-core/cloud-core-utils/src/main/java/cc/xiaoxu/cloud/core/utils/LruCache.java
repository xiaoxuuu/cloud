package cc.xiaoxu.cloud.core.utils;

import java.util.LinkedHashMap;

/**
 * <p>基于 LRU 算法的 map</p>
 *
 * @author 小徐
 * @since 2023/8/17 17:15
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {

    /**
     * 支持的最大数据数量
     */
    private final int maxElements;

    /**
     * 构造方法
     *
     * @param maxElements 最大数据数量
     */
    public LruCache(int maxElements) {
        super(maxElements, 0.75F, true);
        this.maxElements = maxElements;
    }

    protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
        return size() > maxElements;
    }
}
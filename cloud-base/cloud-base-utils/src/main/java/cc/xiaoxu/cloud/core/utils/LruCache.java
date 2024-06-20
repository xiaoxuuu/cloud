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

    /**
     * 当数据超过额定数量时，自动移除最少使用的数据
     * @param eldest The least recently inserted entry in the map, or if
     *           this is an access-ordered map, the least recently accessed
     *           entry.  This is the entry that will be removed if this
     *           method returns {@code true}.  If the map was empty prior
     *           to the {@code put} or {@code putAll} invocation resulting
     *           in this invocation, this will be the entry that was just
     *           inserted; in other words, if the map contains a single
     *           entry, the eldest entry is also the newest.
     * @return 操作师傅成功
     */
    protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
        return size() > maxElements;
    }
}
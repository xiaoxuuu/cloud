package cc.xiaoxu.cloud.core.utils.set;

import java.util.Collections;
import java.util.Set;

public class SetUtils {

    /**
     * 判断两个 Set 是否存在共有数据
     * 使用 JDK 标准库 Collections.disjoint，可读性最高
     */
    public static <T> boolean hasCommonElements(Set<T> set1, Set<T> set2) {
        // 1. 防御性编程：处理 null 情况
        if (set1 == null || set2 == null) {
            return false;
        }

        // 2. disjoint 返回 true 表示“无交集”，所以我们需要取反
        return !Collections.disjoint(set1, set2);
    }
}

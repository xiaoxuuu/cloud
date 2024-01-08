package cc.xiaoxu.cloud.core.utils.set;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>分页工具</p>
 *
 * @author 小徐
 * @since 2023/7/10 14:27
 */
public class PageUtils {

    /**
     * 禁止实例化
     */
    private PageUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 手动分页
     *
     * @param list    待分页集合
     * @param current 当前页数
     * @param size    每页大小
     * @param <T>     类型
     * @return 分页结果
     */
    public static <T> List<T> page(List<T> list, long current, long size) {

        return page(list, (int) current, (int) size);
    }

    /**
     * 手动分页
     *
     * @param list    待分页集合
     * @param current 当前页数
     * @param size    每页大小
     * @param <T>     类型
     * @return 分页结果
     */
    public static <T> List<T> page(List<T> list, int current, int size) {

        if (current < 0 || size < 0) {
            return new ArrayList<>();
        }
        List<List<T>> lists = ListUtils.splitList(list, size);
        if (lists.size() < current) {
            return new ArrayList<>();
        }
        return lists.get(current - 1);
    }
}
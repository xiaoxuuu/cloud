package cc.xiaoxu.cloud.core.utils;

import cc.xiaoxu.cloud.bean.dto.PageDTO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.set.ListUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>分页工具</p>
 *
 * @author 小徐
 * @since 2024/3/20 09:30
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

    public static <T> Page<T> getPage(IPage<?> pageInterface, List<T> list) {

        Page<T> page = new Page<>();
        BeanUtils.populate(pageInterface, page);
        page.setRecords(list);
        return page;
    }

    public static <T> Page<T> getPage(IPage<?> pageInterface, Class<T> clazz) {

        Page<T> page = new Page<>();
        BeanUtils.populate(pageInterface, page);
        List<T> list = new ArrayList<>();
        BeanUtils.populateList(pageInterface.getRecords(), list, clazz);
        page.setRecords(list);
        return page;
    }

    public static <T, R> Page<R> getPage(IPage<T> pageInterface, Function<? super T, ? extends R> mapper) {

        Page<R> page = new Page<>();
        BeanUtils.populate(pageInterface, page);
        List<R> collect = pageInterface.getRecords().stream().map(mapper).collect(Collectors.toList());
        page.setRecords(collect);
        return page;
    }

    public static <T> Page<T> getPageCondition(PageDTO dto) {

        Page<T> page = new Page<>();
        page.setSize(dto.getSize());
        page.setCurrent(dto.getCurrent());
        if (dto.getOrders() != null && !dto.getOrders().isEmpty()) {
            page.addOrder(dto.getOrders().stream().map(k -> k.isAsc() ? OrderItem.asc(k.getColumn()) : OrderItem.desc(k.getColumn())).toList());
        }
        return page;
    }

    public static <T> Page<T> getPageCondition(Long size, Long current) {

        return new Page<T>().setCurrent(current).setSize(size);
    }
}
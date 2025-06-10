package cc.xiaoxu.cloud.core.utils.set;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>集合工具类</p>
 * <p>该工具类下，带返回参数的结果均为重新构建的数据</p>
 *
 * @author 小徐
 * @since 2023/5/11 10:15
 */
public class ListUtils {

    /**
     * 禁止实例化
     */
    private ListUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * <p>集合判空，null 或集合 size 为 0 都返回 true</p>
     *
     * @param coll 带判断集合
     * @return null 或集合 size 为 0 都返回 true
     */
    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * <p>集合判非空，null 或集合 size 为 0 都返回 false</p>
     *
     * @param coll 带判断集合
     * @return null 或集合 size 为 0 都返回 false
     */
    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * <p>将传入的 List 按照给定的 index 拆分成 2 个子 List</p>
     * <p>例：{@code List<Integer> list = List.of(1, 2, 3, 4, 5);}</p>
     * <p>例：{@code subList(list, -1) ==> Exception}</p>
     * <p>例：{@code subList(list, 0) ==> [[], [1, 2, 3, 4, 5]]}}</p>
     * <p>例：{@code subList(list, 1) ==> [[1], [2, 3, 4, 5]]}</p>
     * <p>例：{@code subList(list, 2) ==> [[1, 2], [3, 4, 5]]}</p>
     * <p>例：{@code subList(list, 3) ==> [[1, 2, 3],[4, 5]]}</p>
     * <p>例：{@code subList(list, 6) ==> [[1, 2, 3, 4, 5]]}</p>
     *
     * @param list  待拆分集合
     * @param index 拆分索引位置（包含）
     * @param <T>   类型
     * @return 拆分结果
     */
    public static <T> List<List<T>> divideList(List<T> list, Integer index) {

        if (index < 0) {
            throw new IllegalArgumentException("拆分索引不能小于 0");
        }
        List<List<T>> lists = new ArrayList<>();
        List<T> listLeft = new ArrayList<>();
        List<T> listRight = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (i < index) {
                listLeft.add(list.get(i));
            } else {
                listRight.add(list.get(i));
            }
        }
        lists.add(listLeft);
        lists.add(listRight);
        return lists;
    }

    /**
     * 取出集合指定位置数据
     * <p>例：{@code List<Integer> list = List.of(1, 2, 3, 4, 5);}</p>
     * <p>例：{@code subList(list, 0, 1) ==> [1]}</p>
     * <p>例：{@code subList(list, 0, 2) ==> [1, 2]}</p>
     * <p>例：{@code subList(list, 2, 2) ==> [3, 4]}</p>
     *
     * @param list   原集合
     * @param index  指定位置（包含）
     * @param length 取出个数
     * @param <T>    泛型
     * @return 结果
     */
    public static <T> List<T> subList(List<T> list, int index, int length) {

        if (index < 0) {
            throw new IllegalArgumentException("拆分索引需要大于 0");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("目标集合长度需要大于 0");
        }
        if (list.size() == 0) {
            return new ArrayList<>();
        }

        List<T> returnList = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            returnList.add(list.get(index + i));
        }
        return returnList;
    }

    /**
     * <p>将嵌套集合数据以左上至右下的对角线进行翻转</p>
     * <p>例：</p>
     * <p>{@code List<Integer> listA = List.of(1, 2, 3);}</p>
     * <p>{@code List<Integer> listB = List.of(4, 5, 6);}</p>
     * <p>{@code List<Integer> listC = List.of(7, 8, 9);}</p>
     * <p>{@code List<List<Integer>> lists = List.of(listA, listB, listC);}</p>
     * <p>{@code List<List<Integer>> transpose = XListUtils.transpose(lists);}</p>
     * <p>{@code transpose.get(0) ==> [1, 4, 7]}</p>
     * <p>{@code transpose.get(1) ==> [2, 5, 8]}</p>
     * <p>{@code transpose.get(2) ==> [3, 6, 9]}</p>
     *
     * @param listList 原集合
     * @param <T>      类型
     * @return 翻转后的集合
     */
    public static <T> List<List<T>> transpose(List<List<T>> listList) {

        List<List<T>> newListList = new ArrayList<>();
        for (List<T> list : listList) {
            for (int j = 0; j < list.size(); j++) {
                T t = list.get(j);
                if (newListList.size() <= j) {
                    List<T> newList = new ArrayList<>();
                    newList.add(t);
                    newListList.add(newList);
                } else {
                    List<T> newList = newListList.get(j);
                    newList.add(t);
                    newListList.set(j, newList);
                }
            }
        }
        return newListList;
    }

    /**
     * <p>将一份数据进行多次放置进集合中（浅拷贝）</p>
     *
     * @param t   源数据
     * @param num 重复次数
     * @param <T> 类型
     * @return 集合
     */
    public static <T> List<T> repeat(T t, int num) {

        if (num < 0) {
            throw new IllegalArgumentException("重复次数不能小于 0");
        }
        List<T> list = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            list.add(t);
        }
        return list;
    }

    /**
     * <p>将一份数据进行多次放置进集合中（深拷贝）</p>
     *
     * @param supplier 源数据
     * @param num      重复次数
     * @param <T>      类型
     * @return 集合
     */
    public static <T> List<T> repeat(Supplier<T> supplier, int num) {

        if (num < 0) {
            throw new IllegalArgumentException("重复次数不能小于 0");
        }
        if (Objects.isNull(supplier)) {
            throw new NullPointerException("Supplier 不能为 NULL");
        }
        List<T> list = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            list.add(supplier.get());
        }
        return list;
    }

    /**
     * <p>将传入的 List 按照给定的 size 拆分成多个子 List</p>
     * <p>例如：{@code list = [], per = ? ===>>> []}</p>
     * <p>例如：{@code list = [1, 2, 3, 4, 5], per <= 0 ==> Exception}</p>
     * <p>例如：{@code list = [1, 2, 3, 4, 5], per = 1  ==> [[1], [2], [3], [4], [5]]}</p>
     * <p>例如：{@code list = [1, 2, 3, 4, 5], per = 2  ==> [[1, 2], [3, 4], [5]]}</p>
     * <p>例如：{@code list = [1, 2, 3, 4, 5], per = 3  ==> [[1, 2, 3],[4, 5]]}</p>
     * <p>例如：{@code list = [1, 2, 3, 4, 5], per >= 5  ==> [[1, 2, 3, 4, 5]]}</p>
     *
     * @param list 待分割数据
     * @param per  每个集合大小
     * @param <T>  类型
     * @return 分割结果
     */
    public static <T> List<List<T>> splitList(List<T> list, int per) {

        List<List<T>> returnList = new ArrayList<>();
        if (isEmpty(list)) {
            return returnList;
        }
        if (per <= 0) {
            throw new IllegalArgumentException("拆分索引不能小于 0");
        }
        int count = list.size() / per;
        int yu = list.size() % per;
        for (int i = 0; i <= count; i++) {
            List<T> subList;
            if (i == count) {
                subList = list.subList(i * per, i * per + yu);
            } else {
                subList = list.subList(i * per, per * (i + 1));
            }
            if (isEmpty(subList)) {
                continue;
            }
            returnList.add(subList);
        }
        return returnList;
    }

    /**
     * 数组转集合
     *
     * @param array 数组
     * @param <T>   泛型
     * @return 集合
     */
    public static <T> List<T> create(T[] array) {

        return new ArrayList<>(Arrays.asList(array));
    }

    /**
     * <p>集合相减</p>
     * <p>集合 A - 集合 B</p>
     *
     * @param source      集合 A
     * @param destination 集合 B
     * @param <T>         泛型
     * @return 结果
     */
    public static <T> List<T> reduce(List<T> source, List<T> destination) {

        List<T> result = new ArrayList<>();
        Set<T> destinationSet = new HashSet<>(destination);
        for (T t : source) {
            if (!destinationSet.contains(t)) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * 集合拼接
     *
     * @param collections 待拼接集合
     * @param <T>         集合元素类型
     * @return 拼接好的结果
     */
    @SafeVarargs
    public static <T> List<T> concat(Collection<T>... collections) {

        List<T> list = new ArrayList<>();
        for (Collection<T> tList : collections) {
            if (isEmpty(tList)) {
                continue;
            }
            list.addAll(tList);
        }
        return list;
    }

    /**
     * 将 List 转换为数组
     *
     * @param list  list
     * @param array 目标数组
     * @param <T>   类型
     * @return 结果{@code String[]}
     */
    public static <T> T[] convertToArray(List<T> list, T[] array) {

        if (isEmpty(list)) {
            return array;
        }
        return list.toArray(array);
    }

    /**
     * 取两集合交集
     *
     * @param left  集合1
     * @param right 集合2
     * @param <T>   数据类型
     * @return 相交数据
     */
    public static <T> List<T> intersection(List<T> left, List<T> right) {

        List<T> list = new ArrayList<>();
        if (left == null || right == null || left.isEmpty() || right.isEmpty()) {
            return list;
        }
        HashSet<T> set = new HashSet<>(left);
        right.forEach(k -> {
            if (set.contains(k)) {
                list.add(k);
            }
        });
        return list;
    }

    /**
     * <p>将一个集合按照指定规则重新排序</p>
     * <p>例如：User(int id)</p>
     * <p>{@code List<User> userList = Arrays.asList(new User(7), new User(3), new User(1), new User(2));}</p>
     * <p>{@code List<Integer> idList = Arrays.asList(3, 2, 1);}</p>
     * <p>{@code List<User> sortedList = resort(userList, idList, User::getId);}</p>
     * <p>{@code sortedList：User[3], User[2], User[1], User[7]}</p>
     *
     * @param source       待排序集合
     * @param sortRuleList 排序柜子
     * @param keyMapper    待排序集合所依赖数据
     * @param <T>          泛型
     * @param <K>          泛型，泛型 T 的一个属性
     * @return 排序后集合
     */
    public static <T, K> List<T> resort(List<T> source, List<K> sortRuleList, Function<? super T, ? extends K> keyMapper) {

        List<T> result = new ArrayList<>(source.size());
        Map<? extends K, List<T>> collect = source.stream().collect(Collectors.groupingBy(keyMapper));
        for (K k : sortRuleList) {
            result.addAll(collect.get(k));
            collect.remove(k);
        }
        if (!collect.isEmpty()) {
            collect.forEach((k, v) -> result.addAll(v));
        }
        return result;
    }

    /**
     * <p>从列表的尾部移除所有连续出现的指定元素 t</p>
     * <p>["a", "b"] = removeTail(["a", "b", "c", "c", "c"], "c")</p>
     * <p>[1, 2, 3] = removeTail([1, 2, 3, null, null], null)</p>
     * <p>[] = removeTail([1, 1, 1, 1, 1], 1)</p>
     *
     * @param list 原始集合
     * @param t    需移除数据
     * @return 移除后的数据
     */
    public static <T> List<T> removeTail(List<T> list, T t) {

        int i = list.size() - 1;
        while (i >= 0) {
            if (Objects.isNull(t) ? Objects.isNull(list.get(i)) : (Objects.equals(t, list.get(i)))) {
                i--;
            } else {
                break;
            }
        }
        if (i == -1) {
            return new ArrayList<>();
        }
        List<T> newList = new ArrayList<>(i + 1);
        for (int j = 0; j <= i; j++) {
            newList.add(list.get(j));
        }
        return newList;
    }
}
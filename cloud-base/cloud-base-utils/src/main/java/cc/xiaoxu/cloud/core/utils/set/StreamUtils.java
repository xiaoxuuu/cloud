package cc.xiaoxu.cloud.core.utils.set;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>Stream 工具类</p>
 * <p>对 Stream 进行封装，简化代码</p>
 *
 * @author 小徐
 * @since 2022/12/5 14:04
 */
public class StreamUtils {

    /**
     * 禁止实例化
     */
    private StreamUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * <p>取出集合中的一部分属性</p>
     * <p>将继承自{@link Collection Collection}的集合经过一个操作转换为{@link List List}</p>
     * <p>源集合为 null 或者操作为 null 均默认返回{@link ArrayList ArrayList}</p>
     * <p>例如：{@code List<String> userNameList = XStreamUtils.getList(userList, User::getUserName);}</p>
     *
     * @param collection 源集合
     * @param operation  操作
     * @param <T>        源集合类型
     * @param <R>        取出的字段类型
     * @return 取出的结果
     */
    public static <T, R> List<R> getList(Collection<T> collection, Function<? super T, ? extends R> operation) {

        if (Objects.isNull(collection) || Objects.isNull(operation)) {
            return new ArrayList<>();
        }
        return collection.stream().map(operation).collect(Collectors.toList());
    }

    /**
     * <p>取出集合中的一部分属性，并将其作为 Key 转为 map</p>
     * <p>将继承自{@link Collection Collection}的集合经过一个操作转换为{@link Map Map}</p>
     * <p>源集合为 null 或者操作为 null 均默认返回{@link HashMap HashMap}</p>
     * <p>例如：{@code Map<String, User> userMap = XStreamUtils.getMap(userList, User::getUserName);}</p>
     *
     * @param collection 源集合
     * @param keyMapper  映射关系
     * @param <T>        源集合类型
     * @param <K>        map K 类型
     * @return 取出的结果
     */
    public static <T, K> Map<? extends K, T> getMap(Collection<T> collection, Function<? super T, ? extends K> keyMapper) {

        if (Objects.isNull(collection) || Objects.isNull(keyMapper)) {
            return new HashMap<>(0);
        }
        return collection.stream().collect(Collectors.toMap(keyMapper, a -> a));
    }

    /**
     * <p>将集合进行分组</p>
     * <p>将继承自{@link Collection Collection}的集合经过一个分组操作转换为{@link Map Map}</p>
     * <p>源集合为 null 或者操作为 null 均默认返回{@link HashMap HashMap}</p>
     * <p>例如：{@code Map<String, List<User>> userMap = XStreamUtils.grouping(userList, User::getGender);}</p>
     *
     * @param collection 源集合
     * @param classifier 操作
     * @param <T>        源集合类型
     * @param <K>        取出的字段类型
     * @return 取出的结果
     */
    public static <T, K> Map<K, List<T>> grouping(Collection<T> collection, Function<? super T, ? extends K> classifier) {

        if (Objects.isNull(collection) || Objects.isNull(classifier)) {
            return new HashMap<>(0);
        }
        return collection.stream().collect(Collectors.groupingBy(classifier));
    }
}
package cc.xiaoxu.cloud.my.demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamDemo {

    private static final List<StreamBean> streamBeanList = List.of(
            new StreamBean(1, "Bob", 18, List.of("游泳")),
            new StreamBean(2, "Mark", 18, List.of("游泳")),
            new StreamBean(3, "Anna", 19, List.of("游泳")),
            new StreamBean(4, "Tom", 21, List.of("游泳")));

    public static void main(String[] args) {

        infinite();
    }

    private static void indexed() {
        // TODO 索引访问
        LinkedHashMap<StreamBean, Integer> collect = streamBeanList.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        StreamBean::id,
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new));
        System.out.println();
    }

    public static <T> Collector<T, ?, Integer> sumLength() {
        return Collectors.reducing(0, T::hashCode, Integer::sum);
    }

    private static void customCollectors() {
        // TODO 自定义收集器
        int totalLength = streamBeanList.stream().collect(sumLength());
    }

    private static void infinite() {
        // TODO 模拟滑动窗口
        IntStream.iterate(1, n -> n + 1)
                .limit(10)
                .map(n -> n * n)
                .forEach(System.out::println);
//        // 生成无限流：10 个随机数
        Stream.generate(Math::random)
                .limit(10)
                .forEach(System.out::println);

        // 迭代产生无限流：从 1 循环到 10
        Stream.iterate(1, n -> n + 1)
                .limit(10)
                .forEach(System.out::println);
    }

    /**
     * 条件收集
     */
    private static void partitioningBy() {
        Map<Boolean, List<StreamBean>> collect = streamBeanList.stream()
                .collect(Collectors.partitioningBy(s -> s.age > 1));
    }

    private static void sum() {
        int totalLength = streamBeanList.stream()
                .mapToInt(StreamBean::age)
                .sum();
    }

    private static void collectingAndThen() {
        // 不需要结果的顺序时，可以使用无序流来提高性能。
        List<List<StreamBean>> collect = streamBeanList.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        List::of
                ));
    }

    private static void groupingBy() {
        // 分组返回指定类型 map
        LinkedHashMap<Integer, List<StreamBean>> map = streamBeanList.stream()
                .collect(Collectors.groupingBy(StreamBean::age, LinkedHashMap::new, Collectors.toList()));
        // 多次分组
        Map<Integer, Map<Integer, List<StreamBean>>> collect2 = streamBeanList.stream()
                .collect(Collectors.groupingBy(StreamBean::age, Collectors.groupingBy(StreamBean::id)));
        // 分组并取指定数据
        Map<Integer, List<Integer>> collect = streamBeanList.stream()
                .collect(Collectors.groupingBy(StreamBean::age, Collectors.mapping(StreamBean::age, Collectors.toList())));
        // 分组并计数
        Map<Integer, Long> collect1 = streamBeanList.stream()
                .collect(Collectors.groupingBy(StreamBean::age, Collectors.mapping(a -> a, Collectors.counting())));
    }

    public record StreamBean(Integer id, String name, Integer age, List<String> hobby) {
    }
}
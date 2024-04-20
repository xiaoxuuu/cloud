package cc.xiaoxu.cloud.api.demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamDemo {

    private static final List<StreamBean> streamBeanList = List.of(new StreamBean("Bob", 18, List.of("游泳")),
            new StreamBean("Mark", 18, List.of("游泳")),
            new StreamBean("Anna", 19, List.of("游泳")),
            new StreamBean("Tom", 21, List.of("游泳")));

    public static void main(String[] args) {

        infinite();
    }

    private static void infinite() {

        // 生成无限流：10 个随机数
        Stream.generate(Math::random)
                .limit(10)
                .forEach(System.out::println);

        // 迭代产生无限流：从 1 循环到 10
        Stream.iterate(1, n -> n + 1)
                .limit(10)
                .forEach(System.out::println);
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
        // 分组并取指定数据
        Map<Integer, List<Integer>> collect = streamBeanList.stream()
                .collect(Collectors.groupingBy(StreamBean::age, Collectors.mapping(StreamBean::age, Collectors.toList())));
        // 分组并计数
        Map<Integer, Long> collect1 = streamBeanList.stream()
                .collect(Collectors.groupingBy(StreamBean::age, Collectors.mapping(a -> a, Collectors.counting())));
    }

    public record StreamBean(String name, Integer age, List<String> hobby) {
    }
}
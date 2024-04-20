package cc.xiaoxu.cloud.api.demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamDemo {

    private static final List<StreamBean> streamBeanList = List.of(new StreamBean("Bob", 18, List.of("游泳")),
            new StreamBean("Mark", 18, List.of("游泳")),
            new StreamBean("Anna", 19, List.of("游泳")),
            new StreamBean("Tom", 21, List.of("游泳")));

    public static void main(String[] args) {

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
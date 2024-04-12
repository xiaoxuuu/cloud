package cc.xiaoxu.cloud.api.demo.stream_demo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StreamDemo {

    public static void main(String[] args) {

        List<StreamBean> streamBeanList = List.of(new StreamBean("Bob", 18, List.of("游泳")),
                new StreamBean("Mark", 18, List.of("游泳")),
                new StreamBean("Anna", 19, List.of("游泳")),
                new StreamBean("Tom", 21, List.of("游泳")));

        // 分组返回指定类型 map
        LinkedHashMap<Integer, List<StreamBean>> map = streamBeanList.stream()
                .collect(Collectors.groupingBy(StreamBean::age, LinkedHashMap::new, Collectors.toList()));
    }
}
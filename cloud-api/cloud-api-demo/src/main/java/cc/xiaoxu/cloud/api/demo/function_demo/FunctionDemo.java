package cc.xiaoxu.cloud.api.demo.function_demo;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;

@Slf4j
public class FunctionDemo {

    public static void main(String[] args) {

        test(tran());
    }

    private static void test(Function<List<String>, List<Integer>> function) {

        List<Integer> apply = function.apply(List.of("1", "2"));
        System.out.println(apply.size());
    }

    private static Function<List<String>, List<Integer>> tran() {
        return r -> {
            log.info("{}", r.size());
            return r.stream().map(Integer::parseInt).toList();
        };
    }
}
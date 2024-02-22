package cc.xiaoxu.cloud.api.demo.webClient;

import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
public class Test {

    public static void main(String[] args) {

        List<String> list = List.of();

        String collect = list.stream()
                .map(k -> {
                    try {
                        log.info("开始处理:{}", k);
                        Thread.sleep(new Random().nextInt(1000) + 200);
                        return WebClientDemo.post(k);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(l -> JsonUtils.parse(l, ResultResponse.class))
                .map(ResultResponse::getResult)
                .map(JsonUtils::toString)
                .collect(Collectors.joining(","));
        System.out.println("[" + collect + "]");
    }
}

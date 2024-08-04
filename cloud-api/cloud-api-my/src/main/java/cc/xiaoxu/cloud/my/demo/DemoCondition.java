package cc.xiaoxu.cloud.my.demo;

import cc.xiaoxu.cloud.core.utils.ConditionUtils;
import org.apache.commons.lang3.StringUtils;

public class DemoCondition {

    public static void main(String[] args) {

        String a = "1";
        ConditionUtils.of(a, StringUtils::isNotBlank).handle(System.out::println);
        System.out.println(ConditionUtils.of(a, StringUtils::isNotBlank).get());
        ConditionUtils.of(a, StringUtils::isNotBlank).handle(k -> System.out.println(123), System.out::println);
        ConditionUtils.of(a, StringUtils::isNotBlank).handle(k -> System.out.println(123), System.out::println);

        ConditionUtils.of("", StringUtils::isBlank).orThrow();
        ConditionUtils.of("123", StringUtils::isBlank).orThrow();
    }
}
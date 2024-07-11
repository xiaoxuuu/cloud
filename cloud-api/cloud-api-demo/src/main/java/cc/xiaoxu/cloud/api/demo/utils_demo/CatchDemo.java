package cc.xiaoxu.cloud.api.demo.utils_demo;

import cc.xiaoxu.cloud.core.utils.CatchUtils;

public class CatchDemo {

    public static void main(String[] args) {

        System.out.println("===");
        System.out.println(CatchUtils.of(() -> 1 / 0).handle());
        System.out.println("===");
        System.out.println(CatchUtils.of(() -> 1 / 0).or(() -> 1 + 1).handle());
        System.out.println("===");
        System.out.println(CatchUtils.of(() -> 1 / 0).t(9).handle());
        System.out.println("===");
        System.out.println(CatchUtils.of(() -> 1 / 0).or(() -> 1 + 1).t(9).handle());
        System.out.println("===");
        System.out.println(CatchUtils.of(() -> 1 + 1).t(99).last(() -> System.err.println("完成")).handle());
        System.out.println("===");
    }
}
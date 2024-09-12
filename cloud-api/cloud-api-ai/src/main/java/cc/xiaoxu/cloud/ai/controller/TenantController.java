package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.core.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Slf4j
@RestController
@Tag(name = "租户")
@RequestMapping("/tenant")
public class TenantController {

    public static final Set<String> SET = Set.of("yonsho", "test", "partner", "xc", "496", "134", "973", "147", "952");

    @GetMapping(value = "/check/{tenant}")
    @Operation(summary = "列表")
    public boolean check(@PathVariable("tenant") String tenant) {

        return checkTenant(tenant);
    }

    public static boolean checkTenant(String tenant) {
        return SET.contains(tenant);
    }

    public static void checkTenantThrow(String tenant) {
        if (!checkTenant(tenant)) {
            throw new CustomException("未授权");
        }
    }
}
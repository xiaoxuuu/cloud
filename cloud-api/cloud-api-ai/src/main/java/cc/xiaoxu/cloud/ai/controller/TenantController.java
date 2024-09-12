package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "租户")
@RequestMapping("/tenant")
@AllArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping(value = "/check/{tenant}")
    @Operation(summary = "列表")
    public boolean check(@PathVariable("tenant") String tenant) {

        return tenantService.checkTenant(tenant);
    }
}
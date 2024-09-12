package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.core.cache.CacheService;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class TenantService {

    @Resource
    private CacheService cacheService;

    private List<String> getTenant() {

        String key = "AI:TENANT";
        List<String> cache = JsonUtils.parseArray(cacheService.getCacheObject(key), String.class);
        if (null == cache) {
            cacheService.setCacheObject(key, JsonUtils.toString(List.of("1")));
        } else {
            return cache;
        }
        return JsonUtils.parseArray(cacheService.getCacheObject(key), String.class);
    }

    public boolean checkTenant(String tenant) {

        return getTenant().contains(tenant);
    }

    public void checkTenantThrow(String tenant) {
        if (!checkTenant(tenant)) {
            throw new CustomException("未授权");
        }
    }
}
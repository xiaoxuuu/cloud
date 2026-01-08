package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.my.dao.ConstantMapper;
import cc.xiaoxu.cloud.my.entity.Constant;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Getter
@Slf4j
@Service
public class ConstantService extends ServiceImpl<ConstantMapper, Constant> {

    private final Map<String, String> configMap = new HashMap<>();

    public String getValue(String name) {

        return this.configMap.getOrDefault(name, null);
    }

    @PostConstruct
    public void readConfig() {
        this.configMap.clear();
        this.lambdaQuery().list().forEach(k -> this.configMap.put(k.getName(), k.getValue()));
    }
}
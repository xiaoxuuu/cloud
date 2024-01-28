package cc.xiaoxu.cloud.file.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConditionalOnProperty(prefix = "fileServer", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalFileConfig {

    /**
     * 服务地址
     */
    @Value("${fileServer.local.endpoint}")
    private String host;
}
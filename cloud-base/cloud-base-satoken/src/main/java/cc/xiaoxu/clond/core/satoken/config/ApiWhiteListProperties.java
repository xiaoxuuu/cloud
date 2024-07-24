package cc.xiaoxu.clond.core.satoken.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>白名单</p>
 *
 * @author 小徐
 * @since 2024/7/24 上午11:46
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "api-whitelist")
public class ApiWhiteListProperties {

    /**
     * 白名单url集合
     */
    private List<String> url = new ArrayList<>();

    public String[] getUrlAllArray() {
        return getUrl().toArray(new String[0]);
    }
}
package cc.xiaoxu.cloud.config;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * <p>easy-es 配置</p>
 *
 * @author 小徐
 * @since 2024/3/26 15:24
 */
@AutoConfiguration
@EsMapperScan("cc.xiaoxu.**.dao.es")
@ConditionalOnProperty(value = "easy-es.enable", havingValue = "true")
public class EasyEsConfiguration {

}
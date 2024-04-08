package cc.xiaoxu.cloud.core.config;

import cc.xiaoxu.cloud.core.utils.idUtils.SnowflakeIdUtils;
import com.baomidou.mybatisplus.core.incrementer.IdentifierGenerator;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * <p>自定义 id 生成器</p>
 *
 * @author 小徐
 * @since 2024/4/6 14:06
 */
@Component
@Primary
public class CustomizeIdGenerator implements IdentifierGenerator {

    @Override
    public Number nextId(Object entity) {
        return SnowflakeIdUtils.getInstance().getNextLongId();
    }

}

package cc.xiaoxu.cloud.core.handler;

import cc.xiaoxu.cloud.core.dao.BaseProvider;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

/**
 * MySQL
 */
@Slf4j
@Component
@ConditionalOnExpression("#{environment.getProperty('spring.datasource.url').startsWith('jdbc:mysql')}")
public class MySqlConcat<T> implements SqlConcatHandler<T>, InitializingBean {

    private static final String type = "MySQL";

    @Override
    public void afterPropertiesSet() {
        SqlConcatFactory.register(type, this);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void where(SQL sql, BaseProvider<?> clazz, boolean use, SFunction<T, ?> column, String data) {
        sql.WHERE(clazz.getTablePrefix() + "." + column + " = '" + data + "'");
    }

    @Override
    public void select(SQL sql, BaseProvider<?> clazz, String value, String key) {
        sql.SELECT(clazz.getTablePrefix() + ".`" + value + "` AS `" + key + "`");
    }
}
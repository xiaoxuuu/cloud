package cc.xiaoxu.cloud.core.handler;

import cc.xiaoxu.cloud.core.dao.BaseProvider;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.jdbc.SQL;

public interface SqlConcatHandler<T> {

    String getType();

    void where(SQL sql, BaseProvider<?> clazz, boolean use, SFunction<T, ?> column, String data);

    void select(SQL sql, BaseProvider<?> clazz, String value, String key);
}
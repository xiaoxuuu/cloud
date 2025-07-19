package cc.xiaoxu.cloud.core.handler;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * <p>PostgreSQL 处理 JSON、JSONB</p>
 *
 * @author 小徐
 * @since 2025/7/19 16:59
 */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.OTHER)// JSONB对应JdbcType.OTHER
public class JsonbTypeHandler extends JacksonTypeHandler {

    private static final PGobject jsonObject = new PGobject();
    private static final String JSONB = "jsonb";
    private static final String JSON = "json";

    public JsonbTypeHandler(Class<?> type) {
        super(type);
    }

    /**
     * 写数据库时，把java对象转成JSONB类型
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (ps != null) {
            jsonObject.setType(JSONB);
            jsonObject.setValue(toJson(parameter));
            ps.setObject(i, jsonObject);
        }
    }

    /**
     * 读数据时，把JSONB类型的字段转成java对象
     */
    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object v = rs.getObject(columnName);
        return convertDbToJavaObject(v);
    }

    /**
     * 读数据时，把JSONB类型的字段转成java对象
     */
    @Override
    public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object v = rs.getObject(columnIndex);
        return convertDbToJavaObject(v);
    }

    /**
     * 读数据时，把JSONB类型的字段转成java对象
     */
    @Override
    public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object v = cs.getObject(columnIndex);
        return convertDbToJavaObject(v);
    }

    /**
     * 读数据时，把JSONB类型的字段转成java对象
     */
    private Object convertDbToJavaObject(Object v) {
        if (Objects.isNull(v)) {
            return null;
        }
        if (!PGobject.class.isAssignableFrom(v.getClass())) {
            return v;
        }
        PGobject p = (PGobject) v;
        String type = p.getType();
        if (type == null) {
            return v;
        }
        if (!JSONB.equalsIgnoreCase(type) && !JSON.equalsIgnoreCase(type)) {
            return v;
        }
        String pv = p.getValue();
        if (StringUtils.isBlank(pv)) {
            return v;
        }

        try {
            return parse(pv);
        } catch (Exception e) {
            // 根据实际业务需求，可以选择返回null、抛出异常或返回原始值
            return v;
        }
    }
}
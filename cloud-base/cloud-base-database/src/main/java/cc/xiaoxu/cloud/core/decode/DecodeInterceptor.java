package cc.xiaoxu.cloud.core.decode;

import cc.xiaoxu.cloud.core.exception.CustomException;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {java.sql.Connection.class, Integer.class})})
@Component
public class DecodeInterceptor implements Interceptor {

    private static final Pattern TABLE_NAME_COMPILE = Pattern.compile("(?<=\\(\\s)(?<tableName>.*?)(?=\\sUSING)");
    private static final Pattern SORT_COMPILE = Pattern.compile("(?<=gbk_chinese_ci\\s)(?<sort>.*)(?=\\s*)");

    private static final Logger log = LoggerFactory.getLogger(DecodeInterceptor.class);

    private Boolean confusionEnable = null;

    public void setConfusionEnable(Boolean confusionEnable) {
        // 只允许赋值一次
        if (this.confusionEnable != null) {
            return;
        }
        this.confusionEnable = confusionEnable;
    }

    public static void main(String[] args) {
        String sql = "SELECT DISTINCT \tschool_faculty  FROM \tsz_people_additional_info  WHERE \tpeople_id IN ( \tSELECT DISTINCT \t\tid  \tFROM \t\tsz_people_info  \tWHERE \t( id IN ( SELECT DISTINCT node_id FROM sz_node_school WHERE ( node_table = 'PEOPLE' )) ))  ORDER BY \tschool_faculty ASC, \tCONVERT ( school_faculty USING gbk ) COLLATE gbk_chinese_ci DESC, \tid ASC \tLIMIT 10";
        Set<String> roundMap = new HashSet<>();
        DecodeInterceptor decodeInterceptor = new DecodeInterceptor();
        String preSql = decodeInterceptor.roundConvert(sql, roundMap);
        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(preSql);
            statement.accept(new DecodeVisitor());
        } catch (JSQLParserException e) {
            e.printStackTrace();
            throw new CustomException(e.getMessage());
        }
        String processedSql = decodeInterceptor.restoreConvert(statement.toString(), roundMap);
        System.out.println(processedSql);
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if (confusionEnable == null || !confusionEnable) {
            return invocation.proceed();
        }

        BoundSql boundSql = ((StatementHandler) invocation.getTarget()).getBoundSql();
        String sql = boundSql.getSql();
        log.debug("Original SQL：{}", sql);
        Set<String> tableNameSet = new HashSet<>();
        String preSql = roundConvert(sql, tableNameSet);

        Statement statement;
        try {
            statement = CCJSqlParserUtil.parse(preSql);
            statement.accept(new DecodeVisitor());
        } catch (JSQLParserException e) {
            e.printStackTrace();
            throw new CustomException(e.getMessage());
        }
        log.debug("Encode SQL：{}", statement);
        String processedSql = restoreConvert(statement.toString(), tableNameSet);

        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, processedSql);

        return invocation.proceed();
    }

    /**
     * 绕过 CONVERT
     */
    private String roundConvert(String sql, Set<String> tableNameSet) {

        // CONVERT ( school_faculty USING gbk ) COLLATE gbk_chinese_ci DESC
        String upperCaseSql = sql.toUpperCase();
        int startIndex = upperCaseSql.lastIndexOf("ORDER BY");
        if (startIndex <= 0) {
            return sql;
        }

        // 截取 ORDER BY 方法
        int endIndex = sql.length();
        if (upperCaseSql.contains("LIMIT")) {
            endIndex = upperCaseSql.lastIndexOf("LIMIT");
        }
        String orderBySql = sql.substring(startIndex + 8, endIndex);
        // 寻找所有 CONVERT 方法
        String[] split = orderBySql.split(",");
        for (String s : split) {
            if (!s.contains("CONVERT")) {
                continue;
            }

            Matcher tableNameMatcher = TABLE_NAME_COMPILE.matcher(s);
            if (!tableNameMatcher.find()) {
                continue;
            }
            String tableName = tableNameMatcher.group("tableName");
            Matcher sortMatcher = SORT_COMPILE.matcher(s);
            String sort = "";
            if (sortMatcher.find()) {
                sort = sortMatcher.group("sort");
            }
            String reTableName = tableName + EncodeUtil.CONFUSION_VARIABLE;
            tableNameSet.add(reTableName);
            sql = sql.replace(s, " " + reTableName + " " + sort);
        }
        // 返回数据
        log.info("JSqlParser 不支持的 SQL，手动处理，去除 CONVERT 后 SQL：【{}】", sql);
        return sql;
    }

    /**
     * 恢复 CONVERT
     */
    private String restoreConvert(String sql, Set<String> tableNameSet) {

        if (CollectionUtils.isEmpty(tableNameSet)) {
            return sql;
        }
        // CONVERT ( school_faculty USING gbk ) COLLATE gbk_chinese_ci DESC
        for (String k : tableNameSet) {
            String tableName = k;
            String tableAlias = "";
            if (k.contains(".")) {
                String[] split = k.split("\\.");
                tableAlias = split[0] + ".";
                tableName = split[1];
            }
            String encodeTableName = EncodeUtil.encodeData(tableName);
            String originalTableName = tableName.replace(EncodeUtil.CONFUSION_VARIABLE, "");
            sql = sql.replace(tableAlias + encodeTableName, " CONVERT ( " + tableAlias + EncodeUtil.encodeData(originalTableName) + " USING gbk ) COLLATE gbk_chinese_ci ");
        }
        log.info("JSqlParser 不支持的 SQL，手动处理，恢复 CONVERT 后 SQL：【{}】", sql);
        return sql;
    }
}
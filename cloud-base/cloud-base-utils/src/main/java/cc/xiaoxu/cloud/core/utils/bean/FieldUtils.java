package cc.xiaoxu.cloud.core.utils.bean;

import cc.xiaoxu.cloud.core.bean.func.FunctionGet;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * <p>字段工具类</p>
 *
 * @author 小徐
 * @since 2023/9/22 17:16
 */
public class FieldUtils {

    /**
     * 禁止实例化
     */
    private FieldUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 将 bean 的属性的 get 方法，作为 lambda 表达式传入时，获取 get 方法对应的属性 Field
     *
     * @param fn  lambda 表达式，bean 的属性的 get 方法
     * @param <T> 泛型
     * @param <R> 泛型
     * @return 属性对象
     */
    public static <T, R> Field getField(FunctionGet<T, R> fn) {

        SerializedLambda serializedLambda = getSerializedLambda(fn);
        String fieldName = getFieldName(serializedLambda);
        Field field;
        try {
            field = Class.forName(serializedLambda.getImplClass().replace("/", ".")).getDeclaredField(fieldName);
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return field;
    }

    /**
     * 转换方法引用为属性名
     *
     * @param serializedLambda 序列化 Lambda
     * @return 方法名
     */
    public static String getFieldName(SerializedLambda serializedLambda) {

        // 从 lambda 信息取出 method、field、class 等
        String implMethodName = serializedLambda.getImplMethodName();
        // 确保方法是符合规范的 get 方法，boolean 类型是 is 开头
        if (!implMethodName.startsWith("is") && !implMethodName.startsWith("get")) {
            throw new RuntimeException("get 方法名称: " + implMethodName + ", 不符合 java bean 规范");
        }

        // get 方法开头为 is 或者 get，将方法名 去除 is 或者 get，然后首字母小写，就是属性名
        int prefixLen = implMethodName.startsWith("is") ? 2 : 3;
        String fieldName = implMethodName.substring(prefixLen);
        String firstChar = fieldName.substring(0, 1);
        return fieldName.replaceFirst(firstChar, firstChar.toLowerCase());
    }

    /**
     * 提取 SerializedLambda
     */
    static SerializedLambda getSerializedLambda(Serializable fn) {
        try {
            Method method = fn.getClass().getDeclaredMethod("writeReplace");
            boolean isAccessible = method.isAccessible();
            method.setAccessible(Boolean.TRUE);
            SerializedLambda invoke = (SerializedLambda) method.invoke(fn);
            method.setAccessible(isAccessible);
            return invoke;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
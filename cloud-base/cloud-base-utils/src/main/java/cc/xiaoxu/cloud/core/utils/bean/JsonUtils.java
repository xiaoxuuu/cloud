package cc.xiaoxu.cloud.core.utils.bean;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;

import java.util.List;

/**
 * Json 工具类，基于 <a href="https://github.com/alibaba/fastjson2">fastjson2</a> 封装
 * <p>
 * 进阶：<a href="https://alibaba.github.io/fastjson2/jsonpath_cn">FASTJSON2 JSONPath支持介绍</a>
 * <p>
 * 2022/10/10 16:41
 *
 * @author XiaoXu
 */
public class JsonUtils {

    /**
     * 给定一个类，将其转换为 string，默认不包含类型
     *
     * @param object 原始类
     * @return 序列化后的字符串
     */
    public static String toString(Object object) {

        return toString(object, false);
    }

    /**
     * 给定一个类，将其转换为 string
     *
     * @param object         原始类
     * @param writeClassName 是否将类型写入
     * @return 序列化后的字符串
     */
    public static String toString(Object object, boolean writeClassName) {

        return writeClassName ? JSON.toJSONString(object, JSONWriter.Feature.WriteClassName) : JSON.toJSONString(object);
    }

    /**
     * 给定一个 string 与 class<T>，将其转换为 List<T>，默认不支持多态
     *
     * @param text 序列化后的字符串
     * @param type 反序列化类型，不可以使用父类
     * @param <T>  目标类
     * @return 反序列化结果
     */
    public static <T> List<T> parseArray(String text, Class<T> type) {

        return parseArray(text, type, false);
    }

    /**
     * 给定一个 string 与 class<T>，将其转换为 List<T>
     *
     * @param text            序列化后的字符串
     * @param type            反序列化类型，可以使用父类
     * @param supportAutoType 支持多态序列化
     * @param <T>             目标类
     * @return 反序列化结果
     */
    public static <T> List<T> parseArray(String text, Class<T> type, boolean supportAutoType) {

        return supportAutoType ? JSON.parseArray(text, type, JSONReader.Feature.SupportAutoType) : JSON.parseArray(text, type);
    }

    /**
     * 给定一个 string 与 class<T>，将其转换为 List<T>，默认不支持多态
     *
     * @param text 序列化后的字符串
     * @param type 反序列化类型，不可以使用父类
     * @param <T>  目标类
     * @return 反序列化结果
     */
    public static <T> T parse(String text, Class<T> type) {

        return parse(text, type, false);
    }

    /**
     * 给定一个 string 与 class<T>，将其转换为 T
     *
     * @param text            序列化后的字符串
     * @param type            反序列化类型，可以使用父类
     * @param supportAutoType 支持多态序列化
     * @param <T>             目标类
     * @return 反序列化结果
     */
    public static <T> T parse(String text, Class<T> type, boolean supportAutoType) {

        return supportAutoType ? JSON.parseObject(text, type, JSONReader.Feature.SupportAutoType) : JSON.parseObject(text, type);
    }
}
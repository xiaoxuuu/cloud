package cc.xiaoxu.cloud.core.utils.math;

import cc.xiaoxu.cloud.core.utils.text.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>数学计算基础工具类：提供加减乘除、数值转换</p>
 *
 * @author 小徐
 * @since 2024/4/23 下午2:06
 */
public class BasicMathUtils {

    /**
     * 默认的除法精确度
     */
    public static final int DEF_DIV_SCALE = 20;

    /**
     * 禁止实例化
     */
    BasicMathUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 精确加法运算，计算多个数值总和，null 会被视为 0
     *
     * @param valList 被加数集合
     * @return 两个参数的和(String)
     */
    public static BigDecimal sum(Object... valList) {

        BigDecimal b = BigDecimal.ZERO;
        if (null == valList || valList.length == 0) {
            return b;
        }
        List<Object> list = tranObjToList(valList);
        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        for (Object val : list) {
            b = b.add(cast(val));
        }
        return b;
    }

    /**
     * 精确减法运算，null 会被视为 0
     *
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差(String)
     */
    public static BigDecimal subtract(Object v1, Object v2) {

        if (null == v2) {
            throw new RuntimeException("除数不能为 0");
        }
        return cast(v1).subtract(cast(v2));
    }

    /**
     * 精确乘法运算，计算多个数值的积，null 会被视为 0
     *
     * @param valList 乘数
     * @return 两个参数的积(String)
     */
    public static BigDecimal multiply(Object... valList) {

        BigDecimal b1 = BigDecimal.ZERO;
        if (null == valList || valList.length == 0) {
            return b1;
        }
        b1 = BigDecimal.ONE;
        List<Object> list = tranObjToList(valList);
        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        for (Object val : list) {
            b1 = b1.multiply(cast(val));
        }
        return b1;
    }

    /**
     * （相对）精确除法运算。当发生除不尽情况时，由 scale 参数指 定精度，以后数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位
     * @return 两个参数的商(BigDecimal)
     */
    public static BigDecimal divide(Object v1, Object v2, Integer scale) {

        if (null == v1) {
            return BigDecimal.ZERO;
        }
        if (null == v2) {
            v2 = BigDecimal.ONE;
        }
        if (equal(v2, 0)) {
            throw new IllegalArgumentException("除数不能为 0");
        }
        if (scale < 0) {
            throw new IllegalArgumentException("精确度不能小于 0");
        }

        BigDecimal b1 = cast(v1);
        BigDecimal b2 = cast(v2);
        return b1.divide(b2, scale, RoundingMode.HALF_UP);
    }

    /**
     * （相对）精确除法运算。当发生除不尽情况时，精确到小数点以后 2 位，以后数字四舍五入
     *
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商(String)
     */
    public static BigDecimal divide(Object v1, Object v2) {

        return divide(v1, v2, DEF_DIV_SCALE);
    }

    /**
     * 比较大小
     *
     * @param o1 参数1
     * @param o2 参数2
     * @return o1 等于 o2 返回 true
     */
    public static boolean equal(Object o1, Object o2) {

        return cast(o1).compareTo(cast(o2)) == 0;
    }

    /**
     * 将不同类型数字转为字符串
     *
     * @param o                 数字
     * @param newScale          保留小数位数
     * @param roundingMode      四舍五入规则
     * @param alwaysKeepDecimal 是否严格保留小数位数，0 展示为 0.00
     * @return 格式化结果
     */
    public static String toString(Object o, int newScale, RoundingMode roundingMode, boolean alwaysKeepDecimal) {

        if (null == o) {
            return null;
        }
        BigDecimal bigDecimal = cast(o);
        return alwaysKeepDecimal ?
                bigDecimal.setScale(newScale, roundingMode).toPlainString() :
                bigDecimal.setScale(newScale, roundingMode).stripTrailingZeros().toPlainString();
    }

    /**
     * 将不同类型数字转为字符串
     *
     * @param o                 数字
     * @param newScale          保留小数位数
     * @param alwaysKeepDecimal 是否严格保留小数位数，0 展示为 0.00
     * @return 格式化结果
     */
    public static String toString(Object o, int newScale, boolean alwaysKeepDecimal) {

        if (null == o) {
            return null;
        }
        return toString(o, newScale, RoundingMode.HALF_UP, alwaysKeepDecimal);
    }

    /**
     * 将不同类型数字转为字符串，0 会处理为 0.00
     *
     * @param o        数字
     * @param newScale 保留小数位数
     * @return 结果
     */
    public static String toString(Object o, int newScale) {

        return toString(o, newScale, true);
    }

    /**
     * 将不同类型数字转为字符串，默认保留 2 位小数，0 会处理为 0.00
     *
     * @param o 数字
     * @return 结果
     */
    public static String toString(Object o) {

        if (null == o) {
            return null;
        }
        return toString(o, 2, false);
    }

    /**
     * 将不同类型数字转为数字，直接输出。null 输出为 0
     *
     * @param o 数字
     * @return 结果
     */
    public static Integer toInteger(Object o) {

        if (o == null) {
            return 0;
        }
        return cast(o).intValue();
    }

    /**
     * 将 obj 转为 BigDecimal，null 默认返回 null
     *
     * @param o 参数
     * @return 结果
     */
    public static BigDecimal toBigDecimal(Object o) {

        return cast(o);
    }

    /**
     * <p>将常见数字自动转换为 {@link BigDecimal}</p>
     * <p>当数字类型为{@link Double}时，调用{@link Double#toString(double) Double.toString()}</p>
     * <p>会按 double 的实际能表达的精度对尾数进行了截断。</p>
     *
     * @param obj 参数
     * @return 结果
     */
    protected static BigDecimal cast(Object obj) {

        if (null == obj) {
            throw new RuntimeException("BasicMathUtils.cast obj can not be null");
        }
        if (StringUtils.isBlank(obj.toString())) {
            throw new RuntimeException("BasicMathUtils.cast obj not a number");
        }
        return switch (obj) {
            case String o -> new BigDecimal(o);
            case BigDecimal o -> o;
            case Float o -> BigDecimal.valueOf(o);
            case Double o -> BigDecimal.valueOf(o);
            case Byte o -> new BigDecimal(o.toString());
            case Short o -> new BigDecimal(o.toString());
            case Integer o -> new BigDecimal(o.toString());
            case Long o -> new BigDecimal(o.toString());
            case AtomicInteger o -> new BigDecimal(String.valueOf(((AtomicInteger) obj).get()));
            default -> throw new RuntimeException("数字转换时遇到暂不支持的数据类型");
        };
    }

    /**
     * 将集合拆分转换
     *
     * @param valList 参数
     * @return 结果
     */
    protected static List<Object> tranObjToList(Object... valList) {

        List<Object> list = new ArrayList<>();
        if (null == valList) {
            return list;
        }
        for (Object val : valList) {
            switch (val) {
                case Collection<?> valObjList -> list.addAll(valObjList);
                case Object[] objArray -> list.addAll(Arrays.stream(objArray).toList());
                default -> list.add(val);
            }
        }
        return list;
    }
}
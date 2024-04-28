package cc.xiaoxu.cloud.core.utils.math;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * 数学计算工具类：提供加减乘除、平均值、最大值、最小值计算
 *
 * @author XiaoXu
 * @since 2022/11/9 10:19
 */
public class MathUtils extends BasicMathUtils {

    /**
     * 禁止实例化
     */
    private MathUtils() {
        super();
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 次方
     *
     * @param o   原始数据
     * @param num 次方根，大于等于 0
     * @return 结果
     */
    public static BigDecimal power(Object o, int num) {

        if (0 > num) {
            throw new IllegalArgumentException("次方根 0");
        }
        if (0 == num) {
            return BigDecimal.ONE;
        }
        BigDecimal original = cast(o);
        if (1 == num) {
            return original;
        }
        BigDecimal resultValue = cast(o);
        for (int i = 1; i < num; i++) {
            resultValue = multiply(resultValue, original);
        }
        return resultValue;
    }

    /**
     * 开方（牛顿迭代法）
     *
     * @param value 原数据
     * @param scale 保留小数位数
     * @return 结果
     */
    public static BigDecimal sqrt(Object value, Integer scale) {

        BigDecimal deviationFinal = cast(value);

        BigDecimal num2 = cast(2);
        int precision = 100;
        MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
        int cnt = 0;
        BigDecimal deviation = cast(value);
        while (cnt < precision) {
            deviation = (deviation.add(deviationFinal.divide(deviation, mc))).divide(num2, mc);
            cnt++;
        }
        return deviation.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 开方（牛顿迭代法）
     *
     * @param value 原数据
     * @return 结果
     */
    public static BigDecimal sqrt(Object value) {

        return sqrt(value, DEF_DIV_SCALE);
    }

    /**
     * 平均数
     *
     * @param valList 参数
     * @return 结果
     */
    public static BigDecimal avg(Object... valList) {

        if (null == valList || valList.length == 0) {
            return BigDecimal.ZERO;
        }
        List<Object> list = tranObjToList(valList);
        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return divide(sum(valList), list.size());
    }

    /**
     * 最大值
     *
     * @param valList 参数
     * @return 结果
     */
    public static BigDecimal max(Object... valList) {

        BigDecimal max = BigDecimal.ZERO;
        if (null == valList || valList.length == 0) {
            return max;
        }
        List<Object> list = tranObjToList(valList);
        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        max = cast(list.getFirst());
        for (Object val : list) {
            if (null == val) {
                continue;
            }
            BigDecimal temp = cast(val);
            if (temp.compareTo(max) > 0) {
                max = temp;
            }
        }
        return max;
    }

    /**
     * 最小值
     *
     * @param valList 参数
     * @return 结果
     */
    public static BigDecimal min(Object... valList) {

        BigDecimal min = BigDecimal.ZERO;
        if (null == valList || valList.length == 0) {
            return min;
        }
        List<Object> list = tranObjToList(valList);
        if (list.isEmpty()) {
            return BigDecimal.ZERO;
        }
        min = cast(list.getFirst());
        for (Object val : list) {
            if (null == val) {
                continue;
            }
            BigDecimal temp = cast(val);
            if (temp.compareTo(min) < 0) {
                min = temp;
            }
        }
        return min;
    }

    /**
     * 比较大小
     *
     * @param o1 参数1
     * @param o2 参数2
     * @return o1 大于 o2 返回 true
     */
    public static boolean moreThan(Object o1, Object o2) {

        return cast(o1).compareTo(cast(o2)) > 0;
    }

    /**
     * 比较大小
     *
     * @param o1 参数1
     * @param o2 参数2
     * @return o1 小于 o2 返回 true
     */
    public static boolean lessThan(Object o1, Object o2) {

        return cast(o1).compareTo(cast(o2)) < 0;
    }

    /**
     * 百分比
     * @param o1 数据 1
     * @param o2 数据 2
     * @return 整数百分比，例如：<p>0.01% -> 1</p><p>12.34% -> 1234</p><p>100.00% -> 10000</p>
     */
    public static Integer percentage(Object o1, Object o2) {
        return toInteger(divide(multiply(o1, 10000), o2, 0));
    }

    /**
     * 中位数
     * @param valList 数据
     */
    public static BigDecimal median(Object... valList) {

        List<BigDecimal> bigDecimalList = toBigDecimal(valList).stream().sorted().toList();

        int size = bigDecimalList.size();
        int middle = size / 2;

        // 检查列表大小
        if (size % 2 == 1) {
            // 如果列表大小为奇数，返回中间的元素
            return bigDecimalList.get(middle);
        } else {
            // 如果列表大小为偶数，返回中间两个元素的平均值
            BigDecimal lowerMiddle = bigDecimalList.get(middle - 1);
            BigDecimal upperMiddle = bigDecimalList.get(middle);

            return MathChain.of(lowerMiddle).sum(upperMiddle).divide(2).get();
        }
    }
}
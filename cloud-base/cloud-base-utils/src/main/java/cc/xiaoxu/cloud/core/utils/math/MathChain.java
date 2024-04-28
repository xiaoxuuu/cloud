package cc.xiaoxu.cloud.core.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 数学计算工具类 - 链式编程
 *
 * @author XiaoXu
 * @since 2022/11/9 10:29
 */
public class MathChain {

    /**
     * 计算过程的缓存数据
     */
    private BigDecimal value;

    /**
     * 禁止实例化
     */
    private MathChain() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    public MathChain(Object val) {

        this.value = MathUtils.toBigDecimal(val);
    }

    public static MathChain of(Object val) {

        return new MathChain(val);
    }

    /**
     * 加法，内部调用{@link MathUtils#sum(Object...)}
     *
     * @param valList 被加数集合
     * @return 两个参数的和(String)
     */
    public MathChain sum(Object... valList) {

        this.value = MathUtils.sum(value, valList);
        return this;
    }

    /**
     * 减法，内部调用{@link MathUtils#subtract(Object, Object)}
     *
     * @param v1 减数
     * @return 两个参数的差(String)
     */
    public MathChain subtract(Object v1) {

        this.value = MathUtils.subtract(value, v1);
        return this;
    }

    /**
     * 乘法，内部调用{@link MathUtils#multiply(Object...)}
     *
     * @param valList 乘数
     * @return 两个参数的积(String)
     */
    public MathChain multiply(Object... valList) {

        this.value = MathUtils.multiply(value, valList);
        return this;
    }

    /**
     * 除法，内部调用{@link MathUtils#divide(Object, Object, Integer)}
     *
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位
     * @return 两个参数的商(BigDecimal)
     */
    private MathChain divide(BigDecimal v2, Integer scale) {

        this.value = MathUtils.multiply(value, v2, scale);
        return this;
    }

    /**
     * 除法，内部调用{@link MathUtils#divide(Object, Object)}
     *
     * @param v2 除数
     * @return 两个参数的商(String)
     */
    public MathChain divide(Object v2) {

        this.value = MathUtils.divide(value, v2);
        return this;
    }

    /**
     * 次方，内部调用{@link MathUtils#power(Object, int)}
     *
     * @param num 次方根，大于等于 0
     * @return 结果
     */
    public MathChain power(int num) {

        this.value = MathUtils.power(value, num);
        return this;
    }

    /**
     * 开方（牛顿迭代法），内部调用{@link MathUtils#sqrt(Object)}
     *
     * @return 结果
     */
    public MathChain sqrt() {

        this.value = MathUtils.sqrt(value);
        return this;
    }

    /**
     * 比较大小，内部调用{@link MathUtils#moreThan(Object, Object)}
     *
     * @param o2 参数2
     * @return value 大于 o2 返回 true
     */
    public boolean moreThan(Object o2) {

        return MathUtils.moreThan(value, o2);
    }

    /**
     * 比较大小，内部调用{@link MathUtils#lessThan(Object, Object)}
     *
     * @param o2 参数2
     * @return o1 小于 o2 返回 true
     */
    public boolean lessThan(Object o2) {

        return MathUtils.lessThan(value, o2);
    }

    /**
     * 比较大小，内部调用{@link MathUtils#equal(Object, Object)}
     *
     * @param o2 参数2
     * @return o1 等于 o2 返回 true
     */
    public boolean equal(Object o2) {

        return MathUtils.equal(value, o2);
    }

    /**
     * 格式化为字符串，内部调用{@link MathUtils#toString(Object, int, RoundingMode, boolean)}
     *
     * @param newScale          保留小数位数
     * @param roundingMode      四舍五入规则
     * @param alwaysKeepDecimal 是否严格保留小数位数，0 展示为 0.00
     * @return 格式化结果
     */
    public String toString(int newScale, RoundingMode roundingMode, boolean alwaysKeepDecimal) {

        return MathUtils.toString(value, newScale, roundingMode, alwaysKeepDecimal);
    }

    /**
     * 格式化为字符串，内部调用{@link MathUtils#toString(Object, int, boolean)}
     *
     * @param newScale          保留小数位数
     * @param alwaysKeepDecimal 是否严格保留小数位数，0 展示为 0.00
     * @return 格式化结果
     */
    public String toString(int newScale, boolean alwaysKeepDecimal) {

        return MathUtils.toString(value, newScale, alwaysKeepDecimal);
    }

    /**
     * 格式化为字符串，内部调用{@link MathUtils#toString(Object, int)}
     *
     * @param newScale 保留小数位数
     * @return 结果
     */
    public String toString(int newScale) {

        return MathUtils.toString(value, newScale);
    }

    /**
     * 格式化为字符串，内部调用{@link MathUtils#toString(Object)}
     *
     * @return 结果
     */
    public String format() {

        return MathUtils.toString(value);
    }

    /**
     * FIXME 将不同类型数字转为数字，直接输出。null 输出为 0
     *
     * @return 结果
     */
    public Integer toInteger() {

        return MathUtils.toInteger(value);
    }

    /**
     * 转为 BigDecimal，内部调用{@link MathUtils#toBigDecimal(Object)}
     *
     * @return 结果
     */
    public BigDecimal get() {

        return value;
    }
}
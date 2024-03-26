package cc.xiaoxu.cloud.core.utils.math;

import java.math.BigDecimal;
import java.util.*;

/**
 * 基于 {@link MathUtils} 的公式计算
 * <p>
 * 入参表达式需用空格将所有数字与符号分隔开，例如：25.01 * ( 9 + 0.135 ) / ( 4.111 - 0.111 )
 * <p>
 * 2022/11/9 10:25
 *
 * @author XiaoXu
 */
public class MathFormulaUtils {

    /**
     * 禁止实例化
     */
    private MathFormulaUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    /**
     * 公式计算器
     *
     * @param s 公式，例如：25.01 * ( 9 + 0.135 ) / ( 4.111 - 0.111 )
     * @return 计算结果
     */
    public static BigDecimal calc(String s) {

        // 中缀 -> 后缀
        List<String> transferResult = transferInfillToSuffix(s);
        return multipleCalculate(transferResult);
    }

    /**
     * 表达式计算
     *
     * @param s 参数
     * @return 计算结果
     */
    public static BigDecimal calc(Object... s) {

        // 中缀 -> 后缀
        if (0 == s.length) {
            throw new RuntimeException("Expression cannot be empty");
        }
        Iterator<Object> iterator = Arrays.asList(s).iterator();
        StringBuilder sb = new StringBuilder(objToStr(iterator.next()));
        while (iterator.hasNext()) {
            sb.append(" ");
            sb.append(objToStr(iterator.next()));
        }
        return calc(sb.toString());
    }

    /**
     * 将 {@link Object} 转为 {@link String}
     *
     * @param o 任意被 {@link MathUtils#toString Math.toString} 所支持的类
     * @return 该数据的 {@link String} 表达形式
     */
    private static String objToStr(Object o) {

        if (o instanceof BigDecimal) {
            return MathUtils.toString(o);
        }
        return String.valueOf(o);
    }

    /**
     * 使用栈进行计算，支持 （）
     *
     * @param mathStr 被计算数据
     * @return 计算结果
     */
    private static List<String> transferInfillToSuffix(String mathStr) {

        // 1.初始化一个运算符栈。
        Stack<String> stack = new Stack<>();
        if (mathStr == null || mathStr.length() == 0) {
            return null;
        }
        String[] arr = mathStr.split(" ");

        // 标记输出结果
        List<String> result = new ArrayList<>(arr.length);
        for (String s : arr) {
            // 2.从算数表达式输入的字符串中依次从左向右每次读取一个字符。
            // 3.如果当前字符是操作数，则直接填写到后缀表达式。
            if (!isSymbol(s)) {
                result.add(s);
            } else if ("(".equals(s)) {
                // 4.如果当前字符是（左括号，将其压入运算符栈（第一步定义）。
                stack.push(s);
            } else if ("+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s)) {
                // 5.如果当前字符为运算符，则
                if (!stack.isEmpty()) {
                    String stackTop = stack.pop();
                    // 当此运算符的优先级高于栈顶元素的时候，则将此运算符压入运算符栈
                    if (compare(s, stackTop)) {
                        stack.push(stackTop);
                        stack.push(s);
                    } else {
                        // 否则，弹出栈顶运算符到后缀表达式，并且将当前运算符压栈。回到步骤2.
                        result.add(stackTop);
                        stack.push(s);
                    }
                } else {
                    // 5.1.当运算符栈为空，则将其压入运算符栈。
                    stack.push(s);
                }
            } else if (")".equals(s)) {
                // 6.如果当前字符是）右括号，反复将栈顶元素弹出到后缀表达式，直到栈顶元素是左括号（为止，并将左括号从栈中弹出丢弃。
                while (!stack.isEmpty()) {
                    String item = stack.pop();
                    if (!"(".equals(item)) {
                        result.add(item);
                    } else {
                        break;
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        return result;
    }

    /**
     * 判断字符串是否包含计算符号
     *
     * @param s 待校验字符串
     * @return 结果，包含计算符号返回 true
     */
    private static boolean isSymbol(String s) {

        return "(".equals(s) || ")".equals(s) || "+".equals(s) || "-".equals(s) || "*".equals(s) || "/".equals(s);
    }

    /**
     * 比较计算优先级
     *
     * @param s    被比较元素
     * @param item 栈顶元素
     * @return 此次计算优先级
     */
    private static boolean compare(String s, String item) {
        if ("(".equals(item)) {
            return true;
        }
        if ("*".equals(s) || "/".equals(s)) {
            return "+".equals(item) || "-".equals(item);
        }
        return false;
    }

    /**
     * 计算类
     *
     * @param transferToPostfix 待计算集合
     * @return 计算结果
     */
    private static BigDecimal multipleCalculate(List<String> transferToPostfix) {

        Stack<Object> stack = new Stack<>();
        Object a, b;
        for (String value : transferToPostfix) {
            switch (value) {
                case "+":
                    a = stack.pop();
                    b = stack.pop();
                    stack.push(MathUtils.sum(b, a));
                    break;
                case "-":
                    a = stack.pop();
                    b = stack.pop();
                    stack.push(MathUtils.subtract(b, a));
                    break;
                case "*":
                    a = stack.pop();
                    b = stack.pop();
                    stack.push(MathUtils.multiply(b, a));
                    break;
                case "/":
                    a = stack.pop();
                    b = stack.pop();
                    stack.push(MathUtils.divide(b, a));
                    break;

                default:
                    stack.push(value);
                    break;
            }
        }
        return (BigDecimal) stack.pop();
    }
}
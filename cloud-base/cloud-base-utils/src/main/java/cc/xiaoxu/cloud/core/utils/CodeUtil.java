package cc.xiaoxu.cloud.core.utils;

/**
 * <p>代码工具类</p>
 *
 * @author 小徐
 * @since 2024/5/30 下午2:13
 */
public class CodeUtil {

    /**
     * 获取执行代码行号
     * @return 执行代码行号
     */
    public static int getCurrentLineNumber() {
        return getLineNumber(2);
    }

    /**
     * 获取调用方代码行号
     * @return 调用方代码行号
     */
    public static int getPreviousLineNumber() {
        return getLineNumber(4);
    }

    /**
     * 获取指定栈的代码行号
     * @param i 指定栈层数
     * @return 对应代码行号
     */
    private static int getLineNumber(int i) {
        return Thread.currentThread().getStackTrace()[i].getLineNumber();
    }

    /**
     * 获取执行代码信息
     * @return 执行代码信息
     */
    public static String getCurrentInfo() {
        return getInfo(2);
    }

    /**
     * 获取调用方信息
     * @return 调用方信息
     */
    public static String getPreviousInfo() {
        return getInfo(4);
    }

    /**
     * 获取指定栈内容
     * @param i 指定栈层数
     * @return 对应名称信息
     */
    private static String getInfo(int i) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < i) {
            return "VirtualMachineUtil Error stackTrace.length:" + stackTrace.length + ", i: " + i;
        }
        StackTraceElement stackTraceElement = stackTrace[i];
        return stackTraceElement.getClassName() + "(" + stackTraceElement.getLineNumber() + ")#" + stackTraceElement.getMethodName();
    }
}
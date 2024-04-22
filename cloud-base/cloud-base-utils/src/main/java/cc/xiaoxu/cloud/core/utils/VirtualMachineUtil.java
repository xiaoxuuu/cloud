package cc.xiaoxu.cloud.core.utils;

public class VirtualMachineUtil {

    public static int getCurrentLineNumber() {
        return getLineNumber(2);
    }

    public static int getPreviousLineNumber() {
        return getLineNumber(4);
    }

    private static int getLineNumber(int i) {
        return Thread.currentThread().getStackTrace()[i].getLineNumber();
    }

    public static String getCurrentInfo() {
        return getInfo(2);
    }

    public static String getPreviousInfo() {
        return getInfo(4);
    }

    private static String getInfo(int i) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length < i) {
            return "VirtualMachineUtil Error stackTrace.length:" + stackTrace.length + ", i: " + i;
        }
        StackTraceElement stackTraceElement = stackTrace[i];
        return stackTraceElement.getClassName() + "(" + stackTraceElement.getLineNumber() + ")#" + stackTraceElement.getMethodName();
    }
}

package cc.xiaoxu.cloud.core.utils.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类
 * <p>
 * 2019/12/23 10:40
 *
 * @author Xiao Xu
 */
public class FileUtils {
    /**
     * 禁止实例化
     */
    private FileUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    public static List<String> getFiles(String path) {
        List<String> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        if (null == tempList) {
            return new ArrayList<>();
        }

        for (File value : tempList) {
            if (value.isFile()) {
                files.add(value.getAbsolutePath());
            }
            if (value.isDirectory()) {
                List<String> filesNext = getFiles(value.getAbsolutePath());
                files.addAll(filesNext);
            }
        }
        return files;
    }
}
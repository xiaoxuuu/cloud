package cc.xiaoxu.cloud.core.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文件工具类
 * <p>
 * 2019/12/23 10:40
 *
 * @author Xiao Xu
 */
public class FileUtils {

    private FileUtils() {
    }

    public static List<String> getFiles(String path) {
        List<String> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();

        for (File value : tempList) {
            if (value.isFile()) {
                files.add(value.toString());
                //文件名，不包含路径
                //String fileName = tempList[i].getName();
            }
            if (value.isDirectory()) {
                //这里就不递归了，
            }
        }
        return files;
    }

    /**
     * TODO 获取某文件夹下的文件名和文件内容,存入map集合中
     *
     * @param filePath 需要获取的文件的 路径
     * @return 返回存储文件名和文件内容的map集合
     */
    public static Map<String, String> getFilesDatas(String filePath) {

        Map<String, String> files = new HashMap<>();
        //需要获取的文件的路径
        File file = new File(filePath);
        String[] fileNameLists = file.list(); //存储文件名的String数组
        File[] filePathLists = file.listFiles(); //存储文件路径的String数组
        for (int i = 0; i < filePathLists.length; i++) {
            if (filePathLists[i].isFile()) {
                try {//读取指定文件路径下的文件内容
                    String fileDatas = readFile(filePathLists[i]);
                    //把文件名作为key,文件内容为value 存储在map中
                    files.put(fileNameLists[i], fileDatas);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return files;
    }

    /**
     * TODO 读取指定目录下的文件
     *
     * @param path 文件的路径
     * @return 文件内容
     * @throws IOException
     */
    public static String readFile(File path) throws IOException {
        //创建一个输入流对象
        InputStream is = new FileInputStream(path);
        //定义一个缓冲区
        byte[] bytes = new byte[1024];// 1kb
        //通过输入流使用read方法读取数据
        int len = is.read(bytes);
        //System.out.println("字节数:"+len);
        String str = null;
        while (len != -1) {
            //把数据转换为字符串
            str = new String(bytes, 0, len);
            //System.out.println(str);
            //继续进行读取
            len = is.read(bytes);
        }
        //释放资源
        is.close();
        return str;
    }

    /**
     * @param filePath    写出文件路径
     * @param fileName    写出文件名称
     * @param fileContent 写出文件内容
     * @param append      是否追加，false 则会会自动重命名
     */
    public static void saveFile(String filePath, String fileName, String fileContent, boolean append) throws IOException {

        // 判断文件夹是否存在
        File pathFile = new File(filePath);
        if (!pathFile.exists()) {
            if (!pathFile.mkdirs()) {
                System.out.println("Create path error!");
                return;
            }
        }
        File file = new File(filePath + fileName);
        // 判断文件是否存在
        if (!file.exists() && !file.isDirectory()) {
            if (!file.createNewFile()) {
                System.out.println("Create file error!");
                return;
            }
        } else {
            if (!append) {
                // 追加
                System.out.println("Rename file.");
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                String format = df.format(new Date());
                fileName = fileName + "-" + format;
                file = new File(filePath + fileName);
                if (!file.createNewFile()) {
                    System.out.println("Create new file error!");
                    return;
                }
            }
        }
        FileOutputStream fos = new FileOutputStream(filePath + fileName, true);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write(fileContent.getBytes(StandardCharsets.UTF_8));
        // 强制将缓冲区中的字节一次性写出。
        bos.flush();
        bos.close();
        fos.close();
        System.out.println("Save file over.");
    }
}
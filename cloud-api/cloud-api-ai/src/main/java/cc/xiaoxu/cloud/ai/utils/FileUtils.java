package cc.xiaoxu.cloud.ai.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static String read(String filePath) {

        StringBuilder fileContent = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath);
             // 指定 GBK 编码
//             InputStreamReader isr = new InputStreamReader(fis, "GBK");
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
             // 使用 BufferedReader 逐行读取
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                // 追加每行内容和换行符
                fileContent.append(line).append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent.toString();
    }
}

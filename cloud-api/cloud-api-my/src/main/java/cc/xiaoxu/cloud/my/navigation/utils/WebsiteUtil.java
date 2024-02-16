package cc.xiaoxu.cloud.my.navigation.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebsiteUtil {

    // 处理 301 问题
    public static void main(String[] args) {
        System.out.println("【" + getTitle("https://xiaoxu.cc/") + "】");
    }

    /**
     * 获取网页的标题
     *
     * @param httpUrl 要爬的网页连接
     * @return 网站标题
     */
    public static String getTitle(String httpUrl) {

        // 获取网页的标题的正则表达式
        String searchTitle = "(<title>|<TITLE>)(.*?)(</title>|</TITLE>)";
        // 获得content
        Pattern pattern = Pattern.compile(searchTitle);
        try {
            Matcher matcher = pattern.matcher(getHtmlCode(httpUrl));
            while (matcher.find()) {
                return matcher.group(2);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前网页的code
     *
     * @param httpUrl 网页地址
     * @return 网站标题
     */
    public static String getHtmlCode(String httpUrl) throws IOException {

        // 定义字符串 content
        StringBuilder content = new StringBuilder();
        // 生成传入的 URL 的对象
        URI uri = URI.create(httpUrl);
        URL url = uri.toURL();
        // 获得当前 URL 的字节流（缓冲）
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        String input;
        // 当前行存在数据时
        while ((input = reader.readLine()) != null) {
            // 将读取数据赋给 content
            content.append(input);
        }
        // 关闭缓冲区
        reader.close();
        return content.toString();
    }
}
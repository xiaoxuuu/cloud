package cc.xiaoxu.cloud.my.demo.event_stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class SseMain {

    public static void main(String[] args) {

        get();
    }

    public static void get() {

        try {
            // 生成传入的 URL 的对象
            URI uri = URI.create("http://127.0.0.1:10001/events");
            URL url = uri.toURL();
            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求头
            connection.setRequestProperty("Accept", "text/event-stream");
            connection.setRequestProperty("Cache-Control", "no-cache");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // 获取输入流
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                // 处理每一行数据
                System.out.println(line);
            }

            // 关闭连接
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package cc.xiaoxu.cloud.demo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DemoEventStream {

    public static void main(String[] args) {

        get();
        get2();
    }

    public static void get2() {

        try {
            URL url = URI.create("http://192.168.199.156:8070/telechat/gptDialog/v2").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "text/event-stream");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //构造发送内容
            Map<String, Object> data = new HashMap<>();
            data.put("role", "user");
            data.put("content", "你好啊");
            JSONArray dataInfo = new JSONArray();
            dataInfo.add(data);
            Map<String, Object> messageContent = new HashMap<>(1);
            messageContent.put("dialog", dataInfo);
            JSONObject subscribeMessageJson = new JSONObject(messageContent);

            String s1 = subscribeMessageJson.toString();
            System.out.println("请求:" + s1);
            byte[] bytes = s1.getBytes();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(bytes);
            wr.close();

            int statusCode = connection.getResponseCode();
            log.error("开始");
            InputStream inputStream = statusCode == 200 ? connection.getInputStream() : connection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            int ch;
            while ((ch = reader.read()) != -1) {
                char readChar = (char) ch;
                System.out.print(readChar);
            }
            reader.close();
            inputStream.close();
            connection.disconnect();
            log.error("结束");

        } catch (Exception e) {
            e.printStackTrace();
        }
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

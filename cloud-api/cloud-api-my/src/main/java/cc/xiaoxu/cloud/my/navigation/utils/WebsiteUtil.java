package cc.xiaoxu.cloud.my.navigation.utils;

import cc.xiaoxu.cloud.core.exception.CustomException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class WebsiteUtil {

    public static void main(String[] args) {
        System.out.println("【" + getWebsiteTitle("https://xiaoxu.cc/") + "】");
    }

    public static String getWebsiteTitle(String url) {

        String title = "";
        // 要获取标题的网站 URL
        try {
            URI uri = URI.create(url);
            URL website = uri.toURL();
            URLConnection connection = website.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("<title>")) {
                    int startIndex = line.indexOf("<title>") + 7;
                    int endIndex = line.indexOf("</title>");
                    title = line.substring(startIndex, endIndex);
                    break;
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new CustomException(e.getLocalizedMessage());
        }
        return title;
    }
}
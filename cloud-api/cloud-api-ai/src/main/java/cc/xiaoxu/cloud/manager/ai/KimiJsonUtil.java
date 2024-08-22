package cc.xiaoxu.cloud.manager.ai;

import com.alibaba.fastjson2.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KimiJsonUtil {

    /**
     * 根据List 替换成 kimi的json
     * @param tList          数据集合
     * @param containFields 需要排除的字段
     * @param fileName       文件名
     * @return Json
     */
    public static <T> String getKimiJson(List<T> tList, String fileName, String... containFields) {

        Set<String> excludedFieldsSet = Arrays.stream(containFields).collect(Collectors.toSet());
        StringBuilder content = new StringBuilder();
        for (T item : tList) {
            Field[] fields = item.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (excludedFieldsSet.contains(field.getName())) {
                    field.setAccessible(true);
                    try {
                        content.append(field.getName()).append(": ").append(field.get(item)).append(" \t");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            content.append("\n");
        }

        return getJsonResult(fileName, content);
    }

    /**
     * 根据 JSONObject List 替换成 kimi 的 json
     * @param jsonObjectList          数据集合
     * @param containFields 需要排除的字段
     * @param fileName       文件名
     * @return Json
     */
    public static <T> String getKimiJsonFromJson(List<JSONObject> jsonObjectList, String fileName, String... containFields) {

        Set<String> excludedFieldsSet = Arrays.stream(containFields).collect(Collectors.toSet());
        StringBuilder content = new StringBuilder();
        for (JSONObject jsonObject : jsonObjectList) {
            for (String field : excludedFieldsSet) {
                content.append(field).append(": ").append(jsonObject.get(field)).append(" \t");
            }
            content.append("\n");
        }

        return getJsonResult(fileName, content);
    }

    private static String getJsonResult(String fileName, StringBuilder content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", content.toString());
        jsonObject.put("file_type", "application/zip");
        if (fileName.contains(".xls")) {
            jsonObject.put("filename", fileName);
        } else {
            jsonObject.put("filename", fileName + ".xlsx");
        }
        jsonObject.put("title", "");
        jsonObject.put("type", "file");
        return jsonObject.toJSONString();
    }
}

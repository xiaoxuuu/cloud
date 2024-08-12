package cc.xiaoxu.cloud.core.decode;

import cc.xiaoxu.cloud.core.utils.text.MD5Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EncodeUtil {

    public static final String CONFUSION_VARIABLE = "0B4FD093BD6A97154001542E682A9289";

    private static final Map<String, String> ENCODE_MAP = new HashMap<>();
    private static final Map<String, String> DECODE_MAP = new HashMap<>();

    public static String encodeData(Object data) {

        String encodeString;
        if (!ENCODE_MAP.containsKey(data.toString())) {
            String md5 = MD5Utils.toMd5(data + CONFUSION_VARIABLE);
            StringBuilder stringBuilder = new StringBuilder();
            for (char c : md5.toCharArray()) {
                if (48 <= c && c <= 57) {
                    // 0 ~ 9 映射为 G ～ P
                    stringBuilder.append((char) (c + 23));
                } else {
                    stringBuilder.append(c);
                }
            }
            encodeString = stringBuilder.toString();
            ENCODE_MAP.put(data.toString(), encodeString);
            DECODE_MAP.put(encodeString, data.toString());
        } else {
            encodeString = ENCODE_MAP.get(data.toString());
        }

        log.debug("原数据：{}，编码后数据：{}，编码规则：md5(data + {})", data, encodeString, CONFUSION_VARIABLE);
        return encodeString;
    }

    public static String decodeData(String data) {

        for (String k : DECODE_MAP.keySet()) {
            if (data.contains(k)) {
                data = data.replace(k, DECODE_MAP.get(k));
            }
        }
        return data;
    }
}
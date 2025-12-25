package cc.xiaoxu.cloud.my.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SearchUtils {

    public static boolean containsValue(String value, String keyword) {

        if (StringUtils.isBlank(value)) {
            return false;
        }
        String valueLowerCase = value.toLowerCase();
        String keywordLowerCase = keyword.toLowerCase();
        return valueLowerCase.contains(keywordLowerCase);
    }

    public static boolean containsValue(List<String> value, String keyword) {

        if (CollectionUtils.isEmpty(value)) {
            return false;
        }
        for (String s : value) {
            if (containsValue(s, keyword)) {
                return true;
            }
        }
        return false;
    }
}

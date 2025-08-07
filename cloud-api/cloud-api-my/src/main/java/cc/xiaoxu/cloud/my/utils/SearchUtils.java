package cc.xiaoxu.cloud.my.utils;

import org.apache.commons.lang3.StringUtils;

public class SearchUtils {

    public static boolean containsValue(String value, String keyword) {

        if (StringUtils.isBlank(value)) {
            return false;
        }
        String valueLowerCase = value.toLowerCase();
        String keywordLowerCase = keyword.toLowerCase();
        return valueLowerCase.contains(keywordLowerCase);
    }
}

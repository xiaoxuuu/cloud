package cc.xiaoxu.cloud.core.dao;

import cc.xiaoxu.cloud.core.bean.dto.FieldInfoDTO;
import cc.xiaoxu.cloud.core.decode.EncodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class EncryptionProvider {

    private static String packageString(String str) {

        return " `" + str + "` ";
    }

    public String encryptionField(@Param("tableName") String tableName, @Param("fieldInfoList") List<FieldInfoDTO> fieldInfoList,
                                  @Param("cleanComment") Boolean cleanComment) {

        List<String> list = new ArrayList<>();
        for (FieldInfoDTO column : fieldInfoList) {

            StringBuilder s = new StringBuilder();

            s.append(" CHANGE COLUMN ");
            s.append(packageString(column.getField()));
            s.append(packageString(EncodeUtil.encodeData(column.getField())));
            s.append(column.getType());

            if ("NO".equals(column.getNull())) {
                s.append(" NOT NULL ");
            } else {
                s.append(" NULL ");
            }

            if (StringUtils.isNotBlank(column.getDefault())) {
                HashSet<String> set = new HashSet<>(Arrays.asList("datetime", "timestamp"));
                if (set.contains(column.getType())) {
                    s.append(" DEFAULT ");
                    s.append(column.getDefault().trim());
                } else {
                    s.append(" DEFAULT '");
                    s.append(column.getDefault().trim());
                    s.append("' ");
                }
            }

            if (StringUtils.isNotBlank(column.getExtra())) {
                s.append(" ");
                s.append(column.getExtra().trim());
            }

            if (StringUtils.isNotBlank(column.getField())) {
                s.append(" COMMENT '");
                if (!cleanComment) {
                    s.append(column.getField().trim());
                }
                s.append("'");
            }
            list.add(s.toString());
        }
        return "ALTER TABLE " + packageString(tableName) + " " + String.join(",", list);
    }
}
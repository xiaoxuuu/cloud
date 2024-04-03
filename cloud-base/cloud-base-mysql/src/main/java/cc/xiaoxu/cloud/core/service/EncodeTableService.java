package cc.xiaoxu.cloud.core.service;

import cc.xiaoxu.cloud.core.bean.dto.FieldInfoDTO;
import cc.xiaoxu.cloud.core.dao.EncryptionMapper;
import cc.xiaoxu.cloud.core.decode.EncodeUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>表编码</p>
 *
 * @author 小徐
 * @since 2023/6/13 11:23
 */
@Slf4j
@Service
public class EncodeTableService {

    @Value("${easy.confusion-clean-commit:false}")
    private Boolean cleanComment;

    @Resource
    private EncryptionMapper encryptionMapper;

    @Transactional(rollbackFor = Exception.class)
    public void encodeTable() {

        // 查询所有的表
        List<String> tableList = encryptionMapper.getTableList();

        for (String tableName : tableList) {

            if (!tableName.startsWith("easy") && !tableName.startsWith("sz")) {
                // 跳过已加密表
                log.error("表：{} 已加密，跳过", tableName);
                continue;
            }
            String encryptionTableName = EncodeUtil.encodeData(tableName);
            log.info("当前处理表：{}({})", tableName, encryptionTableName);

            // 查询字段
            List<FieldInfoDTO> tableStructureList = encryptionMapper.getTableStructure(tableName);
            // 编码字段
            encryptionMapper.encryptionField(tableName, tableStructureList, cleanComment);
            // 编码表名
            encryptionMapper.editTableComment(tableName, cleanComment ? "" : tableName);
            encryptionMapper.encryptionTableName(tableName, encryptionTableName);
        }
    }
}
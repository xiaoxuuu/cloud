package cc.xiaoxu.cloud.core.dao;

import cc.xiaoxu.cloud.core.bean.dto.FieldInfoDTO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EncryptionMapper {

    @Select("SHOW TABLES")
    List<String> getTableList();

    @Select("DESC ${tableName}")
    List<FieldInfoDTO> getTableStructure(@Param("tableName") String tableName);

    @UpdateProvider(type = EncryptionProvider.class, method = "encryptionField")
    void encryptionField(@Param("tableName") String tableName, @Param("fieldInfoList") List<FieldInfoDTO> fieldInfoList, @Param("cleanComment") Boolean cleanComment);

    @Update("ALTER TABLE `${tableName}` COMMENT '${comment}'")
    void editTableComment(@Param("tableName") String tableName, @Param("comment") String comment);

    @Update("ALTER TABLE `${oldTableName}` RENAME TO `${newTableName}`")
    void encryptionTableName(@Param("oldTableName") String oldTableName, @Param("newTableName") String newTableName);
}
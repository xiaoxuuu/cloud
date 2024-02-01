package cc.xiaoxu.cloud.api.file.service;

import cc.xiaoxu.cloud.api.file.bean.FileRecord;
import cc.xiaoxu.cloud.api.file.dao.FileRecordMapper;
import cc.xiaoxu.cloud.core.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.constants.DateConstants;
import cc.xiaoxu.cloud.core.utils.text.MD5Utils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class FileRecordService extends ServiceImpl<FileRecordMapper, FileRecord> implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        log.error("使用本地文件服务器");
    }

    public String saveFileRecord(MultipartFile file, String fileName, String relativePath) {
        FileRecord fileRecord = new FileRecord();
        fileRecord.setOriginalName(fileName);
        String suffix = getSuffix(fileName);
        String md5;
        try {
            md5 = MD5Utils.toMd5(file.getInputStream());
        } catch (IOException e) {
            throw new CustomException("未获取到文件名：" + e.getMessage());
        }
        fileRecord.setName(DateUtils.getCurrent(DateConstants.SHORT_TIME_FORMAT) + md5 + "." + suffix);
        fileRecord.setPath(relativePath);
        fileRecord.setSuffix(suffix);
        fileRecord.setMd5(md5);
        fileRecord.setFileSize(String.valueOf(file.getSize()));
        fileRecord.setState(StateEnum.E.getCode());
        save(fileRecord);
        return fileRecord.getId();
    }

    public String getSuffix(String fileName) {
        String[] parts = fileName.split("\\.");
        if (parts.length > 0) {
            return parts[parts.length - 1];
        }
        return "";
    }
}
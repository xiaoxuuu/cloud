package cc.xiaoxu.cloud.file.service;

import cc.xiaoxu.cloud.file.bean.FileVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    /**
     * 获取文件下载地址
     *
     * @return 下载地址
     */
    String getHost();

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 文件路径
     */
    FileVO uploadFile(MultipartFile file);

    /**
     * 批量删除文件
     *
     * @param fileIdList 文件id集合
     */
    void remove(List<String> fileIdList);

    /**
     * 下载文件
     *
     * @param fileId 文件id
     * @return org.springframework.http.ResponseEntity<byte [ ]>
     */
    ResponseEntity<byte[]> download(String fileId);

    /**
     * 文件查询
     */
    FileVO get(String id);
}
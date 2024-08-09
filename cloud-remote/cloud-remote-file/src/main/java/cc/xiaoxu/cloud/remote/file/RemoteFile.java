package cc.xiaoxu.cloud.remote.file;

import cc.xiaoxi.cloud.bean.file.vo.DeleteFileVO;
import cc.xiaoxi.cloud.bean.file.vo.FileVO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface RemoteFile {

    /**
     * 获取文件HOST
     *
     * @return 文件服务HOST
     */
    String getHost();

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件相对路径+名字
     */
    FileVO upload(MultipartFile file);

    /**
     * 删除文件
     *
     * @param  vo 文件id集合
     */
    void delete(DeleteFileVO vo);

    /**
     * 下载文件
     *
     * @param id 文件 id
     * @return 文件流
     */
    ResponseEntity<byte[]> download(String id);

    /**
     * 文件查询
     *
     * @param id 文件id
     * @return 文件
     */
    FileVO get(String id);
}
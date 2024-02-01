package cc.xiaoxu.cloud.api.file.controller;

import cc.xiaoxu.cloud.api.file.bean.DeleteFileVO;
import cc.xiaoxu.cloud.api.file.bean.FileVO;
import cc.xiaoxu.cloud.api.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
@Tag(name = "文件服务")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 获取文件HOST
     *
     * @return 文件服务HOST
     */
    @PostMapping(value = "/host")
    @Operation(summary = "获取文件HOST")
    public String getHost() {
        return fileService.getHost();
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件相对路径+名字
     */
    @PostMapping(value = "/upload")
    @Operation(summary = "上传文件")
    public FileVO upload(@RequestPart(name = "file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    /**
     * 删除文件
     *
     * @param  vo 文件id集合
     */
    @PostMapping(value = "/delete")
    @Operation(summary = "删除文件")
    public void delete(@RequestBody DeleteFileVO vo) {
        fileService.remove(vo.getFileIdList());
    }

    /**
     * 下载文件
     *
     * @param dto 文件名字
     * @return 文件流
     */
    @PostMapping(value = "/download/{id}")
    @Operation(summary = "下载文件")
    public ResponseEntity<byte[]> download(@PathVariable("id") String id) {
        return fileService.download(id);
    }

    /**
     * 文件查询
     *
     * @param id 文件id
     * @return 文件
     */
    @PostMapping(value = "/get/{id}")
    @Operation(summary = "下载文件")
    public FileVO get(@PathVariable("id") String id) {
        return fileService.get(id);
    }
}
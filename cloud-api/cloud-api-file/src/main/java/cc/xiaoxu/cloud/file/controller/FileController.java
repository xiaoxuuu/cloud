package cc.xiaoxu.cloud.file.controller;

import cc.xiaoxu.cloud.file.bean.FileVO;
import cc.xiaoxu.cloud.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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
    public ResultBean<String> getHost() {
        return ResultBean.success("成功", fileService.getHost());
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件相对路径+名字
     */
    @PostMapping(value = "/upload")
    @Operation(summary = "上传文件")
    public ResultBean<FileVO> upload(@RequestPart(name = "file") MultipartFile file) {
        return ResultBean.success("成功", fileService.uploadFile(file));
    }

    /**
     * 文件查询
     */
    @PostMapping(value = "/query")
    @Operation(summary = "文件查询")
    public ResultBean<List<FileRecordVO>> fileQuery(@RequestBody FileQueryVO vo) {
        return ResultBean.success("成功", fileService.fileQuery(vo));
    }

    /**
     * 删除文件
     *
     * @param dto 文件名字集合
     */
    @PostMapping(value = "/delete")
    @Operation(summary = "删除文件")
    public ResultBean<String> delete(@RequestBody FileDTO dto) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException,
            XmlParserException, InternalException {
        fileService.removeObjects(dto.getFileNameList());
        return ResultBean.success();
    }

    /**
     * 下载文件
     *
     * @param dto 文件名字
     * @return 文件流
     */
    @PostMapping(value = "/download")
    @Operation(summary = "下载文件")
    public ResponseEntity<byte[]> download(@RequestBody FileDTO dto) {
        return fileService.download(dto.getFileName());
    }

    /**
     * 获取预览文件地址
     *
     * @param dto 文件名字
     * @return 文件预览路径
     */
    @PostMapping(value = "/preview")
    @Operation(summary = "获取预览文件地址")
    public ResultBean<String> preview(@RequestBody FileDTO dto) {
        return ResultBean.success("成功", fileService.getPreviewUrl(dto.getFileName()));
    }
}
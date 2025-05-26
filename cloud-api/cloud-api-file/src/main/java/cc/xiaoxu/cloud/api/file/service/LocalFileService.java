package cc.xiaoxu.cloud.api.file.service;

import cc.xiaoxi.cloud.bean.file.constant.FileConstants;
import cc.xiaoxi.cloud.bean.file.vo.FileVO;
import cc.xiaoxu.cloud.api.file.config.LocalFileConfig;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.constants.DateConstants;
import cc.xiaoxu.cloud.core.utils.date.DateUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "fileServer", name = "type", havingValue = "local", matchIfMissing = true)
public class LocalFileService implements FileService {

    @Resource
    private LocalFileConfig localFileConfig;

    @Resource
    private FileRecordService fileRecordService;

    @Override
    public String getHost() {
        return localFileConfig.getHost();
    }

    private String getFileDirectory() {

        return Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath() + FileConstants.FILE_SEPARATOR + "static";
    }

    private String getRelativePath() {

        return "localFile" + FileConstants.FILE_SEPARATOR +
                DateUtils.toString(LocalDateTime.now(), DateConstants.YEAR) + FileConstants.FILE_SEPARATOR +
                DateUtils.toString(LocalDateTime.now(), DateConstants.MONTH) + FileConstants.FILE_SEPARATOR +
                DateUtils.toString(LocalDateTime.now(), DateConstants.DAY);
    }

    private String getUploadPath() {
        String path = getFileDirectory() + FileConstants.FILE_SEPARATOR + getRelativePath() + FileConstants.FILE_SEPARATOR;
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new CustomException("目录创建失败：" + path);
            }
        }
        return path;
    }

    @Override
    public FileVO uploadFile(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        OutputStream out = null;

        try {
            // 本地文件保存位置
            String uploadPath = getUploadPath() + FileConstants.FILE_SEPARATOR;
            String relativePath = getRelativePath() + FileConstants.FILE_SEPARATOR + fileName;

            // 保存
            java.io.File outputFile = new java.io.File(uploadPath + FileConstants.FILE_SEPARATOR + fileName);
            out = new FileOutputStream(outputFile);
            out.write(file.getBytes());
            out.close();
            FileVO fileVO = new FileVO(fileName, relativePath, localFileConfig.getHost() + FileConstants.FILE_SEPARATOR + relativePath, localFileConfig.getHost());
            Integer id = fileRecordService.saveFileRecord(file, fileName, relativePath);
            fileVO.setId(id);
            return fileVO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("上传文件失败");
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void remove(List<String> fileIdList) {

        // TODO 删表
        // TODO 删文件
        for (String id : fileIdList) {
            File file = new File(getFileDirectory() + FileConstants.FILE_SEPARATOR + id);
            if (!file.delete()) {
                log.error("local file 删除文件 {} 失败", id);
            }
        }
    }

    @Override
    public ResponseEntity<byte[]> download(String fileId) {
        ResponseEntity<byte[]> responseEntity = null;
        InputStream in = null;
        ByteArrayOutputStream out = null;
        try {
            // 从文件读到servlet response输出流中
            java.io.File file = new java.io.File(fileId);
            in = new FileInputStream(file);
            out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            // 封装返回值
            byte[] bytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
            headers.setContentLength(bytes.length);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setAccessControlExposeHeaders(Collections.singletonList("*"));
            responseEntity = new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseEntity;
    }

    @Override
    public FileVO get(String id) {
        return null;
    }
}

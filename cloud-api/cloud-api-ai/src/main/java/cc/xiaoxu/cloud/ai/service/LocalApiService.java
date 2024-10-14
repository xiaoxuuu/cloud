package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.core.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class LocalApiService {

    @Value("${api.file.location}")
    private String fileLocation;

    // 上传文件至本地
    public String uploadFile1(MultipartFile file) {

//        // 创建文件的存储路径
//        Path path = Paths.get(fileLocation);
//        // 确保目录存在
//        Files.createDirectories(path);
//
//
//        String fileName =  System.currentTimeMillis() + "_" + file.getOriginalFilename();
//
//        // 构建完整的文件路径
//        Path filePath = path.resolve(fileName);
//
//        // 将文件写入到指定路径
//        Files.copy(file.getInputStream(), filePath);
//
//        File dest = new File(filePath);
//        file.transferTo(dest);
        return null;
    }

    // 上传文件至本地
    public String uploadFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new CustomException("Please select a file to upload");
        }

        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            // 创建文件的存储路径
            Path path = Paths.get(fileLocation);
            // 确保目录存在
            Files.createDirectories(path);

            // 获取当前时间并格式化
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String formattedDateTime = now.format(formatter);

            // 在文件名前添加当前时间
            String newFilename = formattedDateTime + "_" + originalFilename;

            // 构建完整的文件路径
            Path filePath = path.resolve(newFilename);

            // 将文件写入到指定路径
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("Failed to upload file " + file.getOriginalFilename());
        }
    }
}
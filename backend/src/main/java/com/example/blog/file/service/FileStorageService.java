package com.example.blog.file.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 文件存储服务
 * 将上传的文件保存到本地文件系统，并返回可访问的 URL
 */
@Service
public class FileStorageService {

    @Value("${blog.upload.base-path:./uploads}")
    private String uploadBasePath;

    private Path markdownPath;
    private Path notebookPath;

    @PostConstruct
    public void init() {
        markdownPath = Paths.get(uploadBasePath, "markdown");
        notebookPath = Paths.get(uploadBasePath, "notebook");
        try {
            Files.createDirectories(markdownPath);
            Files.createDirectories(notebookPath);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录", e);
        }
    }

    /**
     * 保存上传文件
     * @param file 上传的文件
     * @param contentType MARKDOWN 或 NOTEBOOK
     * @return 文件的相对访问路径
     */
    public String storeFile(MultipartFile file, String contentType) {
        String originalFilename = file.getOriginalFilename();
        // 生成唯一文件名
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;

        Path targetDir = "NOTEBOOK".equals(contentType) ? notebookPath : markdownPath;
        Path targetPath = targetDir.resolve(filename);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            // 返回相对路径（前端可通过 /uploads/ 访问）
            String subDir = "NOTEBOOK".equals(contentType) ? "notebook" : "markdown";
            return "/uploads/" + subDir + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("文件存储失败", e);
        }
    }
}

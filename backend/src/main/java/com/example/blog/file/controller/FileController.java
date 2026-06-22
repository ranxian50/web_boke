package com.example.blog.file.controller;

import com.example.blog.common.Result;
import com.example.blog.file.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件接口控制器
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    /** 上传文件 */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "MARKDOWN") String contentType) {
        String filePath = fileStorageService.storeFile(file, contentType);
        return Result.success(Map.of("url", filePath, "fileName", file.getOriginalFilename()));
    }
}

package com.example.server.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件存储服务：保存上传的图片到本地 uploads 目录
 */
@Service
public class FileService {

    private final String uploadDir;

    public FileService(@Value("${file.upload-dir:./uploads/}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * 保存图片文件，返回可访问的相对 URL（/uploads/xxx）
     */
    public String storeImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传文件不能为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("仅支持图片类型文件");
        }

        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        // 重命名文件，避免目录穿越与文件名冲突
        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String ext = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex >= 0) {
            ext = original.substring(dotIndex);
        }
        String filename = System.currentTimeMillis() + "_" + Math.abs(original.hashCode()) + ext;

        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());

        return "/uploads/" + filename;
    }
}

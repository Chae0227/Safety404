package com.webframework.safety404.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDir;

    public FileStorageService(@Value("${file.upload-dir}") String uploadDir) throws IOException {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir); // 폴더 없으면 생성
    }

    // 🔸 파일 저장
    public String storeFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            return null;
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());

        String ext = "";
        int dot = original.lastIndexOf(".");
        if (dot != -1) {
            ext = original.substring(dot);
        }

        String storedFilename = UUID.randomUUID().toString() + ext;

        Path target = this.uploadDir.resolve(storedFilename);
        file.transferTo(target.toFile());

        return storedFilename;
    }

    // 🔸 파일 읽기 (다운로드)
    public Resource loadFileAsResource(String storedFilename) throws MalformedURLException {
        Path file = this.uploadDir.resolve(storedFilename).normalize();
        Resource resource = new UrlResource(file.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("파일을 찾을 수 없습니다: " + storedFilename);
        }

        return resource;
    }
}

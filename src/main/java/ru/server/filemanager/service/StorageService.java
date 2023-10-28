package ru.server.filemanager.service;

import org.springframework.web.multipart.MultipartFile;
import ru.server.filemanager.model.FileMetadata;

import java.util.UUID;

public interface StorageService {
    void createDirectory(String fullPath);
    void saveFile(MultipartFile file, FileMetadata fileMetadata, String fullPath);
    void deleteFile(String fullPath);
    void deleteFolder(String fullPath);
}

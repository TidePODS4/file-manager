package ru.server.filemanager.service;

import org.springframework.web.multipart.MultipartFile;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.model.FileMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public interface FileService {
    Optional<FileMetadata> getFileMetadataById(UUID id);
    FileMetadata uploadFile(MultipartFile file, UUID folderId);
    FileDtoResponse convertToFileDtoResponse(FileMetadata fileMetadata) throws IOException;
    void deleteFile(UUID id);
}

package ru.server.filemanager.service;

import ru.server.filemanager.model.FileMetadata;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public interface FileService {
    File getFileById(UUID id);
    Optional<FileMetadata> getFileMetadataById(UUID id);
    boolean isFileWithSameNameAndFolderExists(FileMetadata fileMetadata);
}

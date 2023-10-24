package ru.server.filemanager.service;

import ru.server.filemanager.model.FileMetadata;

import java.io.File;
import java.util.UUID;

public interface FileMetadataService {
    File getFileById(UUID id);
    boolean isFileWithSameNameAndFolderExists(FileMetadata fileMetadata);
    String getFullPathById(UUID id);
}

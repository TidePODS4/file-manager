package ru.server.filemanager.service;

import ru.server.filemanager.model.FileMetadata;

import java.util.List;
import java.util.UUID;

public interface DirectoryService {
    List<FileMetadata> getFilesMetaByDirectoryId(UUID id);
    List<FileMetadata> getRootFiles();
    FileMetadata create(FileMetadata fileMetadata);
}

package ru.server.filemanager.service;

import org.springframework.core.io.InputStreamSource;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.server.filemanager.model.FileMetadata;

import java.io.File;
import java.util.UUID;

public interface FileMetadataService {
    File getFileById(UUID id);
    boolean isFileWithSameNameAndFolderExists(FileMetadata fileMetadata);
    String getFullPathById(UUID id);
    StreamingResponseBody getFolderZipById(UUID id);
}

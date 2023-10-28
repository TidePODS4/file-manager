package ru.server.filemanager.service;

import org.springframework.hateoas.CollectionModel;
import ru.server.filemanager.dto.request.FolderDtoRequest;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;
import ru.server.filemanager.model.FileMetadata;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DirectoryService {
    List<FileMetadata> getFilesMetaByDirectoryId(UUID directoryId);
    List<FileMetadata> getRootFiles();
    Optional<FileMetadata> getDirectoryById(UUID id);
    FileMetadata create(FileMetadata fileMetadata);
    FileMetadata convertToEntity(FolderDtoRequest folderDto);
    FileDtoResponse convertToFileDtoResponse(FileMetadata fileMetadata) throws IOException;
    FolderDtoResponse convertToFolderDtoResponse(FileMetadata fileMetadata) throws IOException;
    CollectionModel<FileDtoResponse> convertToFileCollectionModel(List<FileDtoResponse> fileDtoResponses) throws IOException;
    void deleteFolder(UUID id);
}

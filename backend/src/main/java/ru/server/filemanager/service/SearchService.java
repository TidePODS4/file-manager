package ru.server.filemanager.service;

import org.springframework.hateoas.CollectionModel;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.model.FileMetadata;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<FileMetadata> searchByName(String name);
    CollectionModel<FileDtoResponse> convertToFileCollectionModel(List<FileDtoResponse> fileDtoResponses,
                                                                  String name)
            throws IOException;
}

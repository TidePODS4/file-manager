package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.SearchService;
import ru.server.filemanager.service.UserService;
import ru.server.filemanager.util.helper.HateoasLinkHelper;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchServiceImp implements SearchService {
    private final FileMetadataRepository fileMetadataRepository;
    private final UserService userService;
    private final HateoasLinkHelper hateoasLinkHelper;

    @Override
    public List<FileMetadata> searchByName(String name) {
        return fileMetadataRepository.findAllByNameContainsIgnoreCaseAndOwnerId(name, getUserId());
    }

    @Override
    public CollectionModel<FileDtoResponse> convertToFileCollectionModel(List<FileDtoResponse> fileDtoResponses,
                                                                         String name)
            throws IOException {
        CollectionModel<FileDtoResponse> collectionModel = CollectionModel.of(fileDtoResponses);
        hateoasLinkHelper.setLinksToSearchResult(collectionModel, name);

        return collectionModel;
    }

    private UUID getUserId(){
        return userService.getUserIdBySecurityContext(
                SecurityContextHolder
                        .getContext()
        );
    }
}

package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.dto.request.FolderDtoRequest;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;
import ru.server.filemanager.exception.DirectoryNotDeletedException;
import ru.server.filemanager.exception.DirectoryNotFoundException;
import ru.server.filemanager.exception.UserDoesNotExistException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.*;
import ru.server.filemanager.util.helper.HateoasLinkHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DirectoryServiceImp implements DirectoryService {
    private final FileMetadataRepository fileMetadataRepository;
    private final FileMetadataService fileMetadataService;
    private final UserService userService;
    private final StorageService storageService;
    private final ModelMapper modelMapper;
    private final HateoasLinkHelper hateoasLinkHelper;
    private final FileService fileService;

    @Override
    public List<FileMetadata> getFilesMetaByDirectoryId(UUID directoryId){
        UUID ownerId = userService.getUserIdBySecurityContext(
                SecurityContextHolder
                        .getContext()
        );

        return fileMetadataRepository.findAllByOwnerIdAndParentId(ownerId, directoryId);
    }

    @Override
    public List<FileMetadata> getRootFiles(){
        UUID ownerId = userService.getUserIdBySecurityContext(
                SecurityContextHolder
                        .getContext()
        );

        return fileMetadataRepository.findAllByOwnerIdAndParentId(ownerId, null);
    }

    @Override
    public Optional<FileMetadata> getDirectoryById(UUID id) {
        return fileMetadataRepository.findFileMetadataById(id);
    }

    @Override
    @Transactional
    public FileMetadata create(FileMetadata fileMetadata) {
        var file = fileMetadataRepository.save(fileMetadata);
        fileMetadataRepository.flush();
        storageService.createDirectory(fileMetadataService.getFullPathById(file.getId()));
        return file;
    }

    @Override
    public FileMetadata convertToEntity(FolderDtoRequest folderDto) {
        var fileMetadata = modelMapper.map(folderDto, FileMetadata.class);
        var userId = userService.getUserIdBySecurityContext(SecurityContextHolder.getContext());
        var userOpt = userService.getUserById(userId);
        fileMetadata.setOwner(userOpt.orElseThrow(
                () -> new UserDoesNotExistException("User "+ userId + " not found")));
        var parent = fileMetadataRepository
                .findFileMetadataById(folderDto.getParentId())
                .orElse(null);

        fileMetadata.setParent(parent);
        return fileMetadata;
    }

    @Override
    public FileDtoResponse convertToFileDtoResponse(FileMetadata fileMetadata) throws IOException {
        FileDtoResponse fileDtoResponse = modelMapper.map(fileMetadata, FileDtoResponse.class);
        hateoasLinkHelper.setLinksToContentFolder(fileDtoResponse);
        return fileDtoResponse;
    }

    @Override
    public FolderDtoResponse convertToFolderDtoResponse(FileMetadata fileMetadata) throws IOException {
        FolderDtoResponse folderDtoResponse = modelMapper.map(fileMetadata, FolderDtoResponse.class);
        var list = new ArrayList<FileDtoResponse>();
        for (FileMetadata metadata : this.getFilesMetaByDirectoryId(fileMetadata.getId())) {
            FileDtoResponse fileDtoResponse = convertToFileDtoResponse(metadata);
            list.add(fileDtoResponse);
        }

        folderDtoResponse.setContent(list);
        hateoasLinkHelper.setLinksToFolder(folderDtoResponse);

        return folderDtoResponse;
    }

    @Override
    public CollectionModel<FileDtoResponse> convertToFileCollectionModel(List<FileDtoResponse> fileDtoResponses) throws IOException {
        CollectionModel<FileDtoResponse> collectionModel = CollectionModel.of(fileDtoResponses);
        hateoasLinkHelper.setLinksToRoot(collectionModel);

        return collectionModel;
    }

    @Override
    @Transactional
    public void deleteFolder(UUID id) {
        var folderOptional = fileMetadataRepository.findFileMetadataById(id);
        var folder = folderOptional.orElseThrow(() -> new DirectoryNotFoundException(
                "Directory " + id + " not found"
        ));

        var path = fileMetadataService.getFullPathById(id);
        try {
            fileMetadataRepository.delete(folder);
        }
        catch (Exception ex) {
            throw new DirectoryNotDeletedException(
                    "Folder " + folder.getName() + " not deleted");
        }

        storageService.deleteFolder(path);
    }
}

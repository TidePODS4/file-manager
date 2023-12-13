package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.dto.request.FileDtoRequest;
import ru.server.filemanager.dto.request.FolderDtoRequest;
import ru.server.filemanager.dto.response.BreadCrumbDto;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;
import ru.server.filemanager.exception.DirectoryNotDeletedException;
import ru.server.filemanager.exception.DirectoryNotFoundException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.*;
import ru.server.filemanager.util.helper.HateoasLinkHelper;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

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

    @Override
    public List<FileMetadata> getFilesMetaByDirectoryId(UUID directoryId){
        UUID ownerId = getUserId();

        var a = fileMetadataRepository.findAllByOwnerIdAndParentId(ownerId, directoryId);
        return fileMetadataRepository.findAllByOwnerIdAndParentId(ownerId, directoryId);
    }

    @Override
    public List<FileMetadata> getRootFiles(){
        UUID ownerId = getUserId();


        return fileMetadataRepository.findAllByOwnerIdAndParentId(ownerId, null);
    }

    @Override
    public Optional<FileMetadata> getDirectoryById(UUID id) {
        UUID ownerId = getUserId();

        return fileMetadataRepository.findFileMetadataByIdAndOwnerId(id, ownerId);
    }

    @Override
    @Transactional
    public FileMetadata create(FileMetadata fileMetadata) {
        fileMetadata.setCreationDate(ZonedDateTime.now());
        fileMetadata.setUpdateDate(fileMetadata.getCreationDate());
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
        fileMetadata.setOwner(userOpt.orElse(userService.addUser(userId)));
        var parent = fileMetadataRepository
                .findFileMetadataById(folderDto.getParentId())
                .orElse(null);

        fileMetadata.setParent(parent);
        return fileMetadata;
    }

    @Override
    public FileDtoResponse convertToFileDtoResponse(FileMetadata fileMetadata) throws IOException {
        FileDtoResponse fileDtoResponse = modelMapper.map(fileMetadata, FileDtoResponse.class);
        hateoasLinkHelper.setLinksToFolderContent(fileDtoResponse);
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
    public CollectionModel<FileDtoResponse> convertToFileCollectionModel(List<FileDtoResponse> fileDtoResponses)
            throws IOException {
        CollectionModel<FileDtoResponse> collectionModel = CollectionModel.of(fileDtoResponses);
        hateoasLinkHelper.setLinksToRoot(collectionModel);

        return collectionModel;
    }

    @Override
    public FileMetadata convertToEntity(FileDtoRequest fileDtoRequest) {
        var fileMetadata = modelMapper.map(fileDtoRequest, FileMetadata.class);
        var userId = userService.getUserIdBySecurityContext(SecurityContextHolder.getContext());
        var userOpt = userService.getUserById(userId);
        fileMetadata.setOwner(userOpt.orElse(userService.addUser(userId)));

        FileMetadata parent;

        if (fileDtoRequest.getParentId() == null){
            parent = null;
        } else {
            parent = fileMetadataRepository
                    .findFileMetadataById(fileDtoRequest.getParentId())
                    .orElseThrow(() -> new DirectoryNotFoundException(
                            "Directory " + fileDtoRequest.getParentId() + " not found"));
        }

        fileMetadata.setParent(parent);
        return fileMetadata;
    }


    @Override
    @Transactional
    public void deleteFolder(UUID id) {
        UUID ownerId = getUserId();
        var folderOptional = fileMetadataRepository.findFileMetadataByIdAndOwnerId(id, ownerId);
        var folder = folderOptional.orElseThrow(() -> new DirectoryNotFoundException(
                "Directory " + id + " not found"
        ));

        var path = fileMetadataService.getFullPathById(id);
        try {
            folder.setParent(null);
            fileMetadataRepository.delete(folder);
        }
        catch (Exception ex) {
            throw new DirectoryNotDeletedException(
                    "Folder " + folder.getName() + " not deleted");
        }

        storageService.deleteFolder(path);
    }

    @Override
    public List<BreadCrumbDto> getBreadCrumbsByFolderId(UUID id) {
        UUID ownerId = getUserId();

        List<BreadCrumbDto> breadcrumbs = new ArrayList<>();
        FileMetadata folder = fileMetadataRepository.findFileMetadataByIdAndOwnerId(id, ownerId).orElse(null);
        while (folder != null) {
            breadcrumbs.add(new BreadCrumbDto(folder.getId(), folder.getName()));
            if (folder.getParent() != null){
                folder = fileMetadataRepository.findFileMetadataByIdAndOwnerId(
                        folder.getParent().getId(), ownerId).orElse(null);
            }
            else folder = null;
        }

        Collections.reverse(breadcrumbs);

        return breadcrumbs;
    }

    @Override
    @Transactional
    public FileMetadata update(FileMetadata fileMetadata, UUID dirId) {
        var metadata =
                fileMetadataRepository.findFileMetadataByIdAndOwnerId(dirId, getUserId())
                        .orElseThrow(() ->
                        new DirectoryNotFoundException("Folder " + dirId + " not found"));

        metadata.setName(fileMetadata.getName());
        fileMetadata.setUpdateDate(ZonedDateTime.now());

        return fileMetadataRepository.save(metadata);
    }

    private UUID getUserId(){
        return userService.getUserIdBySecurityContext(
                SecurityContextHolder
                        .getContext()
        );
    }
}

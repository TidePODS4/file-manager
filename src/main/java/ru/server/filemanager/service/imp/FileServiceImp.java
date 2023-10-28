package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.exception.DirectoryNotFoundException;
import ru.server.filemanager.exception.FileNotFoundException;
import ru.server.filemanager.exception.UserDoesNotExistException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.FileMetadataService;
import ru.server.filemanager.service.FileService;
import ru.server.filemanager.service.StorageService;
import ru.server.filemanager.service.UserService;
import ru.server.filemanager.util.helper.HateoasLinkHelper;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImp implements FileService {
    private final FileMetadataRepository fileMetadataRepository;
    private final StorageService storageService;
    private final FileMetadataService fileMetadataService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final HateoasLinkHelper hateoasLinkHelper;

    @Override
    public Optional<FileMetadata> getFileMetadataById(UUID id){
        return fileMetadataRepository.findFileMetadataById(id);
    }

    @Override
    @Transactional
    public FileMetadata uploadFile(MultipartFile file, UUID folderId) {
        var userId = userService.getUserIdBySecurityContext(SecurityContextHolder.getContext());
        var userOpt = userService.getUserById(userId);

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setName(file.getOriginalFilename());
        fileMetadata.setFolder(false);
        fileMetadata.setSize(file.getSize());
        fileMetadata.setOwner(userOpt.orElseThrow(
                () -> new UserDoesNotExistException("User "+ userId + " not found")));
        fileMetadata.setParent(fileMetadataRepository.findFileMetadataById(folderId).orElseThrow(
                () -> new DirectoryNotFoundException("Directory " + folderId + " not found")
        ));

        fileMetadataRepository.save(fileMetadata);
        fileMetadataRepository.flush();

        storageService.saveFile(file, fileMetadata,
                fileMetadataService.getFullPathById(fileMetadata.getId()));

        return fileMetadata;
    }

    @Override
    public FileDtoResponse convertToFileDtoResponse(FileMetadata fileMetadata) throws IOException {
        var fileMeta = modelMapper.map(fileMetadata, FileDtoResponse.class);
        hateoasLinkHelper.setLinksToFile(fileMeta);
        return fileMeta;
    }

    @Override
    @Transactional
    public void deleteFile(UUID id) {
        var fileMetadataOptional = fileMetadataRepository.findFileMetadataById(id);
        var fileMetadata = fileMetadataOptional.orElseThrow(
                () -> new FileNotFoundException("File " + id + " not found"));

        var path = fileMetadataService.getFullPathById(id);
        fileMetadataRepository.delete(fileMetadata);
        fileMetadataRepository.flush();

        storageService.deleteFile(path);
    }
}

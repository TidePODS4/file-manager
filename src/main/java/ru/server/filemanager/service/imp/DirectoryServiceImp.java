package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.dto.FolderDto;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.model.User;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.*;

import java.util.List;
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
    @Transactional
    public FileMetadata create(FileMetadata fileMetadata) {
        var file = fileMetadataRepository.save(fileMetadata);
        fileMetadataRepository.flush();
        var a = fileMetadataService.getFullPathById(file.getId());
        storageService.createDirectory(fileMetadataService.getFullPathById(file.getId()));
        return file;
    }

    @Override
    public FileMetadata convertToEntity(FolderDto folderDto) {
        return modelMapper.map(folderDto, FileMetadata.class);
    }
}

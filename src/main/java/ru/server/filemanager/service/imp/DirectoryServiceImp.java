package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.model.User;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.UserService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DirectoryServiceImp implements DirectoryService {
    private final FileMetadataRepository fileMetadataRepository;
    private final UserService userService;

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

//    @Override
//    @Transactional
//    public FileMetadata create(FileMetadata fileMetadata) {
//        return fileMetadataRepository.save(fileMetadata);
//    }
}

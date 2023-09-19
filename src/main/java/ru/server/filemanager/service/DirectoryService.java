package ru.server.filemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DirectoryService {
    private final FileMetadataRepository fileMetadataRepository;

    @Autowired
    public DirectoryService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public List<FileMetadata> getFilesMetaByDirectoryId(UUID id){
        return fileMetadataRepository.findAllByParentId(id);
    }

    public List<FileMetadata> getRootFiles(){
        return fileMetadataRepository.findAllByParentId(null);
    }
}

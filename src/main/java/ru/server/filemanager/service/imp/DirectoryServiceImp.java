package ru.server.filemanager.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.DirectoryService;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DirectoryServiceImp implements DirectoryService {
    private final FileMetadataRepository fileMetadataRepository;

    @Autowired
    public DirectoryServiceImp(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    @Override
    public List<FileMetadata> getFilesMetaByDirectoryId(UUID id){
        return fileMetadataRepository.findAllByParentId(id);
    }

    @Override
    public List<FileMetadata> getRootFiles(){
        return fileMetadataRepository.findAllByParentId(null);
    }

    @Override
    @Transactional
    public FileMetadata create(FileMetadata fileMetadata) {
        return fileMetadataRepository.save(fileMetadata);
    }
}

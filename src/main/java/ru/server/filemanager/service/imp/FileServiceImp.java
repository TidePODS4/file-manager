package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.FileService;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImp implements FileService {
    private final FileMetadataRepository fileMetadataRepository;
    private final JdbcTemplate jdbcTemplate;
    @Value("${files.paths.download-dir}")
    private String downloadDir;

    @Override
    public File getFileById(UUID id) {
        var path = downloadDir + getFullPathById(id);
        return new File(path);
    }

    @Override
    public Optional<FileMetadata> getFileMetadataById(UUID id){
        return fileMetadataRepository.findFileMetadataById(id);
    }

    private String getFullPathById(UUID id){
        return (jdbcTemplate.queryForObject(
                "SELECT get_full_path(?)", String.class, id));
    }

    @Override
    public boolean isFileWithSameNameAndFolderExists(FileMetadata fileMetadata) {
        return fileMetadataRepository.existsFileMetadataByNameAndParentId(
                fileMetadata.getName(), fileMetadata.getParentId());
    }

    /*public FileMetadata save(FileMetadata fileMetadata){
        var file = fileMetadataRepository.save(fileMetadata);


    }*/
}

package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.FileMetadataService;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileMetadataServiceImp implements FileMetadataService {
    private final FileMetadataRepository fileMetadataRepository;
    private final JdbcTemplate jdbcTemplate;
    @Value("${files.paths.download-dir}")
    private String downloadDir;

    @Override
    public File getFileById(UUID id) {
        var path = getFullPathById(id);
        return new File(path);
    }

    @Override
    public boolean isFileWithSameNameAndFolderExists(FileMetadata fileMetadata) {
        return fileMetadataRepository.existsFileMetadataByNameAndParentId(
                fileMetadata.getName(), fileMetadata.getParent() != null ?
                        fileMetadata.getParent().getId() : null);
    }

    @Override
    public String getFullPathById(UUID id){
        return downloadDir + (jdbcTemplate.queryForObject(
                "SELECT get_full_path(?)", String.class, id));
    }
}

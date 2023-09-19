package ru.server.filemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.server.filemanager.repository.FileMetadataRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class FileService {
    private final FileMetadataRepository fileMetadataRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileService(FileMetadataRepository fileMetadataRepository, JdbcTemplate jdbcTemplate) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public File getFileById(UUID id) {

        var path = "D:\\file_server\\" + getFullPathById(id);
        return (new File(path));
    }

    public String getFullPathById(UUID id){
        return (jdbcTemplate.queryForObject(
                "SELECT get_full_path(?)", String.class, id));
    }

    public String getFileMimeType(File file) throws IOException {
        String mimeType = Files.probeContentType(Paths.get(file.getPath()));

        return mimeType == null ? "application/octet-stream" : mimeType;
    }
}

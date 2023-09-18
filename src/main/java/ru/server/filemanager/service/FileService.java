package ru.server.filemanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.server.filemanager.entity.FileMetadata;
import ru.server.filemanager.repository.FileRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FileService(FileRepository fileRepository, JdbcTemplate jdbcTemplate) {
        this.fileRepository = fileRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FileMetadata> getFilesInDirectory(FileMetadata directory){
        return fileRepository.findAllByParentId(directory.getId());
    }

    @Async
    public CompletableFuture<Resource> getFileById(UUID id) throws ExecutionException,
            InterruptedException, FileNotFoundException {

        var file = fileRepository.getReferenceById(id);
        var path = getFullPathByFile(file);
        return CompletableFuture.completedFuture(
                new InputStreamResource(
                        new FileInputStream(path.get())));
    }

    @Async
    protected CompletableFuture<String> getFullPathByFile(FileMetadata file){
        return CompletableFuture.completedFuture(jdbcTemplate.queryForObject(
                "SELECT get_full_path(?)", String.class,
                file.getId()));
    }
}

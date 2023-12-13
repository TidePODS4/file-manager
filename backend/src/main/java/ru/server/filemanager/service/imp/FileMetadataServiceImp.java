package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.server.filemanager.exception.ArchiveCreationException;
import ru.server.filemanager.exception.DirectoryNotFoundException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.repository.FileMetadataRepository;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.FileMetadataService;
import ru.server.filemanager.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class FileMetadataServiceImp implements FileMetadataService {
    private final FileMetadataRepository fileMetadataRepository;
    private final JdbcTemplate jdbcTemplate;
    private final UserService userService;
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

    @Override
    public StreamingResponseBody getFolderZipById(UUID id) {
        var folderPath = getFullPathById(id);

        var folderMetadata = fileMetadataRepository.findFileMetadataByIdAndOwnerId(id, getUserId())
                .orElseThrow(() -> new DirectoryNotFoundException("Directory " + id + " not found"));

        Path zipPath;
        File zipFile;

        try {
            zipPath = Files.createTempFile(folderMetadata.getId().toString(), ".zip");
            zipFile = zipPath.toFile();
        } catch (IOException e) {
            throw new ArchiveCreationException("Archive for directory " + id + " not created");
        }

        try (
                ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))
        ) {
            Path sourcePath = Paths.get(folderPath);

            String sourceFolderName = fileMetadataRepository.findFileMetadataByIdAndOwnerId(
                            UUID.fromString(sourcePath.getFileName().toString()), getUserId())
                    .orElseThrow(() -> new DirectoryNotFoundException("Directory " + id + " not found"))
                    .getName();
            try (var paths = Files.walk(sourcePath)){
                paths.forEach(path -> {
                    StringBuilder relPath = new StringBuilder();
                    var relativize = sourcePath.relativize(path);
                    for (var rel : relativize){

                        if (rel.toString().equals("")){
                            continue;
                        }

                        var fileMeta = fileMetadataRepository.findFileMetadataByIdAndOwnerId(
                                        UUID.fromString(rel.toString()), getUserId())
                                .orElseThrow(() -> new ArchiveCreationException(
                                        "Archive for directory " + id + " not created"));

                        if (fileMeta.isFolder()){
                            relPath.append(fileMeta.getName()).append("\\");
                        }
                        else {
                            relPath.append(fileMeta.getName());
                        }
                    }

                    String zipEntryName = sourceFolderName + "\\" + relPath;

                    ZipEntry zipEntry = new ZipEntry(zipEntryName);
                    try {
                        zos.putNextEntry(zipEntry);
                        if (!Files.isDirectory(path)) {
                            Files.copy(path, zos);
                        }
                        zos.closeEntry();
                    } catch (IOException e) {
                        throw new ArchiveCreationException("Archive for directory " + id + " not created");
                    }
                });

            }

            return outputStream -> {
                int bytesRead;
                byte[] buffer = new byte[8192];
                try (InputStream inputStream = new FileInputStream(zipFile)) {
                    while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    throw new ArchiveCreationException("Archive for directory " + id + " not created");
                }

                Files.deleteIfExists(zipPath);
            };
        } catch (IOException e) {
            throw new ArchiveCreationException("Archive for directory " + id + " not created");
        }
    }

    private UUID getUserId(){
        return userService.getUserIdBySecurityContext(
                SecurityContextHolder
                        .getContext()
        );
    }
}

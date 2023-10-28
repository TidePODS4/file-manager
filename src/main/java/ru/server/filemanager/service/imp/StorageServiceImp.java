package ru.server.filemanager.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.server.filemanager.exception.*;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.FileMetadataService;
import ru.server.filemanager.service.StorageService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImp implements StorageService {
    @Override
    public void createDirectory(String fullPath) {
        Path path = Paths.get(fullPath);
        if (Files.exists(path))
            throw new DirectoryAlreadyExistsException(
                    "Directory " + fullPath + " already exists");

        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new DirectoryNotCreatedException(
                    "Directory " + fullPath + " not created");
        }
    }

    @Override
    public void saveFile(MultipartFile file, FileMetadata fileMetadata, String fullPath) {
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(fullPath);
            Files.write(path, bytes);
        } catch (Exception e) {
            throw new FileNotUploadedException("File " + file.getOriginalFilename() + " not uploaded");
        }
    }

    @Override
    public void deleteFile(String fullPath) {
        Path path = Paths.get(fullPath);
        if (!Files.exists(path))
            throw new FileNotDeletedException("File not deleted");
        File file = new File(fullPath);

        boolean isDeleted = file.delete();

        if (!isDeleted) {
            throw new FileNotDeletedException("File not deleted");
        }
    }

    @Override
    public void deleteFolder(String fullPath) {
        File directory = new File(fullPath);
        if (!directory.exists())
            throw new DirectoryNotFoundException("Folder " + fullPath + " not found");

        File[] entries = directory.listFiles();

        if (entries != null) {
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    deleteFolder(entry.getAbsolutePath());
                } else {
                    entry.delete();
                }
            }
        }

        directory.delete();
    }
}

package ru.server.filemanager.service.imp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.server.filemanager.exception.DirectoryAlreadyExistsException;
import ru.server.filemanager.exception.DirectoryNotCreatedException;
import ru.server.filemanager.service.StorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageServiceImp implements StorageService {
    @Override
    public void createDirectory(String fullPath) {
        Path path = Paths.get(fullPath);
        if (Files.exists(path))
            throw new DirectoryAlreadyExistsException(
                    "Directory " + fullPath + " already exists");

        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            throw new DirectoryNotCreatedException(
                    "Directory " + fullPath + " not created");
        }
    }
}

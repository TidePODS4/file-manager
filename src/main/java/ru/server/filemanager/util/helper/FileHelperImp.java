package ru.server.filemanager.util.helper;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class FileHelperImp implements FileHelper{
    @Override
    public String getFileMimeType(File file) throws IOException {
        String mimeType = Files.probeContentType(Paths.get(file.getPath()));
        return mimeType == null ? "application/octet-stream" : mimeType;
    }
}

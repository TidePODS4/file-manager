package ru.server.filemanager.util.helper;

import java.io.File;
import java.io.IOException;

public interface FileHelper {
    String getFileMimeType(String filePath) throws IOException;
}

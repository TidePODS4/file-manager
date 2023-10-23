package ru.server.filemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.server.filemanager.service.imp.DirectoryServiceImp;
import ru.server.filemanager.service.FileService;
import ru.server.filemanager.service.imp.FileServiceImp;
import ru.server.filemanager.util.helper.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileManagerController {
    private final FileService fileService;
    private final FileHelper fileHelper;

    @Autowired
    public FileManagerController(FileServiceImp fileService,
                                 DirectoryServiceImp directoryService,
                                 FileHelper fileHelper) {
        this.fileService = fileService;
        this.fileHelper = fileHelper;
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> getFileDownloadById(@PathVariable("id") UUID id) throws IOException {

        return getResourceResponseEntity(id, true);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> getFilePreviewById(@PathVariable("id") UUID id) throws IOException {

        return getResourceResponseEntity(id, false);
    }

    private ResponseEntity<Resource> getResourceResponseEntity(
            @PathVariable("id") UUID id, boolean isDownload) throws IOException {
        File file = fileService.getFileById(id);
        String mimeType = fileHelper.getFileMimeType(file);
        String headerType = isDownload ? "attachment" : "inline";

        Resource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        headerType + "; filename=\"" + file.getName() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
    }
}

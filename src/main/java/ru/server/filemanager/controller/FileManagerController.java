package ru.server.filemanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.server.filemanager.exception.FileNotFoundException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.FileMetadataService;
import ru.server.filemanager.service.imp.DirectoryServiceImp;
import ru.server.filemanager.service.FileService;
import ru.server.filemanager.service.imp.FileServiceImp;
import ru.server.filemanager.util.helper.FileHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileManagerController {
    private final FileService fileService;
    private final FileMetadataService fileMetadataService;
    private final FileHelper fileHelper;

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

        Optional<FileMetadata> fileMetadataOpt = fileService.getFileMetadataById(id);
        FileMetadata fileMetadata = fileMetadataOpt.orElseThrow(
                () -> new FileNotFoundException("File " + id + " not found")
        );

        File file = fileMetadataService.getFileById(id);
        String mimeType = fileHelper.getFileMimeType(fileMetadata.getName());
        String headerType = isDownload ? "attachment" : "inline";

        Resource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        headerType + "; filename=\"" + fileMetadata.getName() + "\"")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType(mimeType))
                .body(resource);
    }
}

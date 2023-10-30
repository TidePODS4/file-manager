package ru.server.filemanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.server.filemanager.dto.request.FolderDtoRequest;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.exception.DirectoryNotCreatedException;
import ru.server.filemanager.exception.FileNotFoundException;
import ru.server.filemanager.exception.UserDoesNotExistException;
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

    @GetMapping("/{id}")
    public ResponseEntity<FileDtoResponse> getFileMetadataById(@PathVariable("id") UUID id) throws IOException {
        var metadata = fileService.getFileMetadataById(id)
                .orElseThrow(() -> new FileNotFoundException("File " + id + " not found"));

        return ResponseEntity.ok(fileService.convertToFileDtoResponse(metadata));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> getFileDownloadById(@PathVariable("id") UUID id) throws IOException {
        return getResourceResponseEntity(id, true);
    }

    @GetMapping("/{id}/preview")
    public ResponseEntity<Resource> getFilePreviewById(@PathVariable("id") UUID id) throws IOException {
        return getResourceResponseEntity(id, false);
    }

    @PostMapping()
    public ResponseEntity<FileDtoResponse> uploadFile(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("folder_id") UUID folderId) throws IOException {
        var metadata = fileService.uploadFile(file, folderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileService.convertToFileDtoResponse(metadata));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable("id") UUID id){
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
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

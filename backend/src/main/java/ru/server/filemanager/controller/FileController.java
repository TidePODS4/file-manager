package ru.server.filemanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.server.filemanager.dto.request.FileDtoRequest;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.exception.*;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.FileMetadataService;
import ru.server.filemanager.service.FileService;
import ru.server.filemanager.util.ErrorBuilder;
import ru.server.filemanager.util.helper.FileHelper;
import ru.server.filemanager.util.validator.FileMetadataValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final DirectoryService directoryService;
    private final FileMetadataService fileMetadataService;
    private final FileHelper fileHelper;
    private final FileMetadataValidator fileMetadataValidator;
    private final ErrorBuilder errorBuilder;

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

    @PutMapping("/{id}")
    public ResponseEntity<FileDtoResponse> updateFile(@PathVariable("id") UUID id,
                                                        @RequestBody @Valid FileDtoRequest fileDtoRequest,
                                                        BindingResult bindingResult) throws IOException {
        var metadata = directoryService.convertToEntity(fileDtoRequest);
        fileMetadataValidator.validate(metadata, bindingResult);

        if (bindingResult.hasErrors()){
            throw new FileNotUpdatedException(errorBuilder.buildErrorMsg(bindingResult));
        }

        var metadataResponse = fileService.update(metadata, id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(directoryService.convertToFileDtoResponse(metadataResponse));
    }
}

package ru.server.filemanager.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.server.filemanager.dto.request.FileDtoRequest;
import ru.server.filemanager.dto.request.FolderDtoRequest;
import ru.server.filemanager.dto.response.BreadCrumbDto;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;
import ru.server.filemanager.exception.DirectoryNotCreatedException;
import ru.server.filemanager.exception.DirectoryNotFoundException;
import ru.server.filemanager.exception.DirectoryNotUpdatedException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.FileMetadataService;
import ru.server.filemanager.service.FileService;
import ru.server.filemanager.util.ErrorBuilder;
import ru.server.filemanager.util.validator.FileMetadataValidator;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class DirectoryController {
    private final DirectoryService directoryService;
    private final FileMetadataService fileMetadataService;
    private final FileMetadataValidator fileMetadataValidator;
    private final ErrorBuilder errorBuilder;
    private final FileService fileService;

    @GetMapping()
    public ResponseEntity<CollectionModel<FileDtoResponse>> getRootFiles() throws IOException{
        var filesMetadata = directoryService.getRootFiles();
        var dtoResponse = new ArrayList<FileDtoResponse>();
        for (FileMetadata fileMetadata : filesMetadata) {
            FileDtoResponse fileDtoResponse = directoryService.convertToFileDtoResponse(fileMetadata);
            dtoResponse.add(fileDtoResponse);
        }

        return ResponseEntity.ok(directoryService.convertToFileCollectionModel(dtoResponse));
    }

    @GetMapping("/{id}/breadcrumbs")
    public ResponseEntity<List<BreadCrumbDto>> getBreadCrumbsByFolderId(@PathVariable("id") UUID id){
        var breadcrumbs = directoryService.getBreadCrumbsByFolderId(id);
        return ResponseEntity.ok(breadcrumbs);
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<FolderDtoResponse> getFilesInDirectory(@PathVariable("id") UUID id) throws IOException {
        var fileMetadata = directoryService.getDirectoryById(id)
                .orElseThrow(() -> new DirectoryNotFoundException("Directory " + id + " not found"));

        return ResponseEntity.ok(directoryService.convertToFolderDtoResponse(fileMetadata));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileDtoResponse> getDirectoryById(@PathVariable("id") UUID id) throws IOException {
        var fileMetadata = directoryService.getDirectoryById(id)
                .orElseThrow(() -> new DirectoryNotFoundException("Directory " + id + " not found"));
        var dtoResponse = directoryService.convertToFileDtoResponse(fileMetadata);
        return ResponseEntity.ok(dtoResponse);
    }

    @PostMapping()
    public ResponseEntity<FileDtoResponse> createNewFolder(@RequestBody @Valid FolderDtoRequest folderDto,
                                                        BindingResult bindingResult) throws IOException {
        return getFileDtoResponseResponseEntity(folderDto, bindingResult);
    }

    @PostMapping("/{id}")
    public ResponseEntity<FileDtoResponse> createNewFolderInFolder(@RequestBody @Valid FolderDtoRequest folderDto,
                                                                   BindingResult bindingResult,
                                                                   @PathVariable("id") UUID parentId)
            throws IOException {
        folderDto.setParentId(parentId);
        return getFileDtoResponseResponseEntity(folderDto, bindingResult);
    }

    private ResponseEntity<FileDtoResponse> getFileDtoResponseResponseEntity(@RequestBody @Valid FolderDtoRequest folderDto, BindingResult bindingResult) throws IOException {
        var fileMetadata = directoryService.convertToEntity(folderDto);

        fileMetadataValidator.validate(fileMetadata, bindingResult);

        if (bindingResult.hasErrors()){
            throw new DirectoryNotCreatedException(errorBuilder.buildErrorMsg(bindingResult));
        }

        var folderMetadata = directoryService.create(fileMetadata);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(directoryService.convertToFileDtoResponse(folderMetadata));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable("id") UUID id){
        directoryService.deleteFolder(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/test")
    public String test(){
        return HttpStatus.BAD_REQUEST.getReasonPhrase();
    }

    @PostMapping("/{id}/upload-file")
    public ResponseEntity<FileDtoResponse> uploadFile(@RequestPart("file") MultipartFile file,
                                                      @PathVariable("id") UUID folderId) throws IOException {
        var metadata = fileService.uploadFile(file, folderId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileService.convertToFileDtoResponse(metadata));
    }

    @PostMapping("/upload-file")
    public ResponseEntity<FileDtoResponse> uploadFileToRoot(@RequestPart("file") MultipartFile file)
            throws IOException {
        var metadata = fileService.uploadFile(file, null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileService.convertToFileDtoResponse(metadata));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FileDtoResponse> updateFolder(@PathVariable("id") UUID id,
                                                        @RequestBody @Valid FileDtoRequest fileDtoRequest,
                                                        BindingResult bindingResult) throws IOException {
        var metadata = directoryService.convertToEntity(fileDtoRequest);
        fileMetadataValidator.validate(metadata, bindingResult);

        if (bindingResult.hasErrors()){
            throw new DirectoryNotUpdatedException(errorBuilder.buildErrorMsg(bindingResult));
        }

        var metadataResponse = directoryService.update(metadata, id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(directoryService.convertToFileDtoResponse(metadataResponse));
    }

    @GetMapping("/{id}/download")
    public StreamingResponseBody downloadFolder(HttpServletResponse response, @PathVariable("id") UUID id){
        var fileMetadata = directoryService.getDirectoryById(id)
                .orElseThrow(() -> new DirectoryNotFoundException("Directory " + id + " not found"));

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" +
                fileMetadata.getName() + ".zip");

        return fileMetadataService.getFolderZipById(id);
    }
}

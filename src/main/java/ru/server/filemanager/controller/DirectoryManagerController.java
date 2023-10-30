package ru.server.filemanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.server.filemanager.dto.request.FolderDtoRequest;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;
import ru.server.filemanager.exception.DirectoryNotCreatedException;
import ru.server.filemanager.exception.DirectoryNotFoundException;
import ru.server.filemanager.exception.UserDoesNotExistException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.FileService;
import ru.server.filemanager.service.UserService;
import ru.server.filemanager.util.ErrorBuilder;
import ru.server.filemanager.util.validator.FileMetadataValidator;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class DirectoryManagerController {
    private final DirectoryService directoryService;
    private final UserService userService;
    private final FileMetadataValidator fileMetadataValidator;
    private final ErrorBuilder errorBuilder;
    private final FileService fileService;

    @GetMapping()
//    @PreAuthorize("hasRole(T(ru.server.filemanager.util.Role).ADMIN.getRole())")
    public ResponseEntity<CollectionModel<FileDtoResponse>> getRootFiles() throws IOException{
        var fileMetadata = directoryService.getRootFiles();
        var dtoResponse = new ArrayList<FileDtoResponse>();
        for (FileMetadata fileMetadatum : fileMetadata) {
            FileDtoResponse fileDtoResponse = directoryService.convertToFileDtoResponse(fileMetadatum);
            dtoResponse.add(fileDtoResponse);
        }

        return ResponseEntity.ok(directoryService.convertToFileCollectionModel(dtoResponse));
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
}

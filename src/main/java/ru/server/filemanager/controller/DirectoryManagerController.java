package ru.server.filemanager.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.imp.DirectoryServiceImp;
import ru.server.filemanager.dto.ErrorResponse;
import ru.server.filemanager.exception.FileNotUploadedException;
import ru.server.filemanager.util.validator.FileMetadataValidator;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/folders")
public class DirectoryManagerController {
    private final DirectoryService directoryService;
    private final FileMetadataValidator fileMetadataValidator;

    @Autowired
    public DirectoryManagerController(DirectoryServiceImp directoryService,
                                      FileMetadataValidator fileMetadataValidator) {
        this.directoryService = directoryService;
        this.fileMetadataValidator = fileMetadataValidator;
    }

    @GetMapping()
    @PreAuthorize("hasRole(T(ru.server.filemanager.util.Role).ADMIN.getRole())")
    public List<FileMetadata> getRootFiles(){
        return directoryService.getRootFiles();
    }

    @GetMapping("/{id}")
    public List<FileMetadata> getFilesInDirectory(@PathVariable("id") UUID id){
        return directoryService.getFilesMetaByDirectoryId(id);
    }

    @PostMapping()
    public ResponseEntity<FileMetadata> createNewFolder(@RequestBody @Valid FileMetadata fileMetadata,
                                                        BindingResult bindingResult){
        fileMetadataValidator.validate(fileMetadata, bindingResult);

        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();

            for (var error : errors){
                errorMsg.append(error.getField()).append(" - ")
                        .append(error.getDefaultMessage()).append(";");
            }

            throw new FileNotUploadedException(errorMsg.toString());
        }

        fileMetadata.setFolder(true);
        var folderMetadata = directoryService.create(fileMetadata);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(folderMetadata);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(FileNotUploadedException ex){
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

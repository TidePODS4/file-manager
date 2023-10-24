package ru.server.filemanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.server.filemanager.dto.FolderDto;
import ru.server.filemanager.exception.DirectoryNotCreatedException;
import ru.server.filemanager.exception.UserDoesNotExistException;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.UserService;
import ru.server.filemanager.dto.ErrorResponse;
import ru.server.filemanager.exception.FileNotUploadedException;
import ru.server.filemanager.util.ErrorBuilder;
import ru.server.filemanager.util.validator.FileMetadataValidator;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/folders")
@RequiredArgsConstructor
public class DirectoryManagerController {
    private final DirectoryService directoryService;
    private final UserService userService;
    private final FileMetadataValidator fileMetadataValidator;
    private final ErrorBuilder errorBuilder;

    @GetMapping()
//    @PreAuthorize("hasRole(T(ru.server.filemanager.util.Role).ADMIN.getRole())")
    public ResponseEntity<List<FileMetadata>> getRootFiles(){
        return ResponseEntity.ok(directoryService.getRootFiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<FileMetadata>> getFilesInDirectory(@PathVariable("id") UUID id){
        return ResponseEntity.ok(directoryService.getFilesMetaByDirectoryId(id));
    }

    @PostMapping()
    public ResponseEntity<FileMetadata> createNewFolder(@RequestBody @Valid FolderDto folderDto,
                                                        BindingResult bindingResult){
        var fileMetadata = directoryService.convertToEntity(folderDto);
        var userId = userService.getUserIdBySecurityContext(SecurityContextHolder.getContext());
        var userOpt = userService.getUserById(userId);
        fileMetadata.setOwner(userOpt.orElseThrow(
                () -> new UserDoesNotExistException("User "+ userId + " not found")));

        fileMetadataValidator.validate(fileMetadata, bindingResult);

        if (bindingResult.hasErrors()){
            throw new DirectoryNotCreatedException(errorBuilder.buildErrorMsg(bindingResult));
        }

        var folderMetadata = directoryService.create(fileMetadata);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(folderMetadata);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFolder(@PathVariable("id") UUID id){
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public String test(){
        return HttpStatus.BAD_REQUEST.getReasonPhrase();
    }

//    @ExceptionHandler
//    private ResponseEntity<ErrorResponse> handleException(FileNotUploadedException ex){
//        ErrorResponse response = new ErrorResponse(
//                HttpStatus.BAD_REQUEST.getReasonPhrase(),
//                ex.getMessage(),
//                System.currentTimeMillis()
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
//    }
}

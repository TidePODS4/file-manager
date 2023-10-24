package ru.server.filemanager.util.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.imp.FileServiceImp;

@Component
public class FileMetadataValidator implements Validator {
    private final FileServiceImp fileService;

    @Autowired
    public FileMetadataValidator(FileServiceImp fileService) {
        this.fileService = fileService;
    }


    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return FileMetadata.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        FileMetadata file = (FileMetadata) target;

        if (fileService.getFileMetadataById(file.getParentId()).isEmpty()){
            errors.rejectValue("parentId", "", "This file does not exist.");
        }

        if (fileService.isFileWithSameNameAndFolderExists(file)){
            errors.rejectValue("name", "",
                    "File with this name in current directory already exists");
        }
    }
}

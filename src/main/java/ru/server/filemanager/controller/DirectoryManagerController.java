package ru.server.filemanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.DirectoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class DirectoryManagerController {
    private final DirectoryService directoryService;

    @Autowired
    public DirectoryManagerController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping()
    public List<FileMetadata> getRootFiles(){
        return directoryService.getRootFiles();
    }

    @GetMapping("/folder/{id}")
    public List<FileMetadata> getFilesInDirectory(@PathVariable("id") UUID id){
        return directoryService.getFilesMetaByDirectoryId(id);
    }
}

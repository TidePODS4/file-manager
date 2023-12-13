package ru.server.filemanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.service.DirectoryService;
import ru.server.filemanager.service.SearchService;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final DirectoryService directoryService;

    @GetMapping()
    public ResponseEntity<CollectionModel<FileDtoResponse>> searchByName(@RequestParam("q") String name)
            throws IOException {
        var files = searchService.searchByName(name);
        var dtoResponse = new ArrayList<FileDtoResponse>();

        for (var file : files){
            dtoResponse.add(directoryService.convertToFileDtoResponse(file));
        }

        return ResponseEntity.ok(searchService.convertToFileCollectionModel(dtoResponse, name));
    }
}

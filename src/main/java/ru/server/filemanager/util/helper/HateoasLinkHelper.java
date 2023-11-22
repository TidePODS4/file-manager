package ru.server.filemanager.util.helper;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import ru.server.filemanager.controller.DirectoryManagerController;
import ru.server.filemanager.controller.FileManagerController;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;
import ru.server.filemanager.service.FileService;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HateoasLinkHelper {
    public void setLinksToContentFolder(FileDtoResponse fileDtoResponse) throws IOException {
        Link selfLink = linkTo(methodOn(DirectoryManagerController.class)
                .getDirectoryById(fileDtoResponse.getId()))
                .withSelfRel();

        Link contentLink = linkTo(methodOn(DirectoryManagerController.class)
                .getFilesInDirectory(fileDtoResponse.getId()))
                .withRel("content");

        Link parentLink = linkTo(methodOn(DirectoryManagerController.class)
                .getDirectoryById(fileDtoResponse.getParentId()))
                .withRel("parent");

        Link breadcrumbsLink = linkTo(methodOn(DirectoryManagerController.class)
                .getBreadCrumbsByFolderId(fileDtoResponse.getId()))
                .withRel("breadcrumbs");

        fileDtoResponse.add(selfLink, parentLink, contentLink, breadcrumbsLink);
    }

    public void setLinksToFile(FileDtoResponse fileDtoResponse) throws IOException {
        Link selfLink = linkTo(methodOn(FileManagerController.class)
                .getFileMetadataById(fileDtoResponse.getId()))
                .withSelfRel();

        Link downloadLink = linkTo(methodOn(FileManagerController.class)
                .getFileDownloadById(fileDtoResponse.getId()))
                .withRel("download");

        Link previewLink = linkTo(methodOn(FileManagerController.class)
                .getFilePreviewById(fileDtoResponse.getId()))
                .withRel("preview");

        fileDtoResponse.add(selfLink, downloadLink, previewLink);
    }

    public void setLinksToFolder(FolderDtoResponse folderDtoResponse) throws IOException{
        Link selfLink = linkTo(methodOn(DirectoryManagerController.class)
                .getDirectoryById(folderDtoResponse.getId()))
                .withSelfRel();

        Link parentLink;

        if (folderDtoResponse.getParentId() == null){
            parentLink = linkTo(methodOn(DirectoryManagerController.class)
                    .getRootFiles())
                    .withRel("parent");
        }
        else {
            parentLink = linkTo(methodOn(DirectoryManagerController.class)
                    .getDirectoryById(folderDtoResponse.getParentId()))
                    .withRel("parent");
        }

        Link contentLink = linkTo(methodOn(DirectoryManagerController.class)
                .getFilesInDirectory(folderDtoResponse.getId()))
                .withRel("content");

        Link breadcrumbsLink = linkTo(methodOn(DirectoryManagerController.class)
                .getBreadCrumbsByFolderId(folderDtoResponse.getId()))
                .withRel("breadcrumbs");

        folderDtoResponse.add(selfLink, parentLink, contentLink, breadcrumbsLink);
    }

    public void setLinksToRoot(CollectionModel<FileDtoResponse> collectionModel) throws IOException {
        Link selfLink = linkTo(methodOn(DirectoryManagerController.class)
                .getRootFiles())
                .withSelfRel();

        collectionModel.add(selfLink);
    }
}

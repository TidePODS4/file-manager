package ru.server.filemanager.util.helper;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;
import ru.server.filemanager.controller.DirectoryController;
import ru.server.filemanager.controller.FileController;
import ru.server.filemanager.controller.SearchController;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;

import java.io.IOException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class HateoasLinkHelper {
    public void setLinksToFolderContent(FileDtoResponse fileDtoResponse) throws IOException {
        if (!fileDtoResponse.isFolder()){
            setLinksToFile(fileDtoResponse);
        } else{
            Link selfLink = linkTo(methodOn(DirectoryController.class)
                    .getDirectoryById(fileDtoResponse.getId()))
                    .withSelfRel();

            Link contentLink = linkTo(methodOn(DirectoryController.class)
                    .getFilesInDirectory(fileDtoResponse.getId()))
                    .withRel("content");
            Link breadcrumbsLink = linkTo(methodOn(DirectoryController.class)
                    .getBreadCrumbsByFolderId(fileDtoResponse.getId()))
                    .withRel("breadcrumbs");

            Link downloadLink = linkTo(methodOn(DirectoryController.class)
                    .downloadFolder(null, fileDtoResponse.getId()))
                    .withRel("download");

            fileDtoResponse.add(selfLink, contentLink, breadcrumbsLink, downloadLink);
        }


        Link parentLink = linkTo(methodOn(DirectoryController.class)
                .getDirectoryById(fileDtoResponse.getParentId()))
                .withRel("parent");

        fileDtoResponse.add(parentLink);
    }

    public void setLinksToFile(FileDtoResponse fileDtoResponse) throws IOException {
        Link selfLink = linkTo(methodOn(FileController.class)
                .getFileMetadataById(fileDtoResponse.getId()))
                .withSelfRel();

        Link downloadLink = linkTo(methodOn(FileController.class)
                .getFileDownloadById(fileDtoResponse.getId()))
                .withRel("download");

        Link previewLink = linkTo(methodOn(FileController.class)
                .getFilePreviewById(fileDtoResponse.getId()))
                .withRel("preview");

        fileDtoResponse.add(selfLink, downloadLink, previewLink);
    }

    public void setLinksToFolder(FolderDtoResponse folderDtoResponse) throws IOException{
        Link selfLink = linkTo(methodOn(DirectoryController.class)
                .getDirectoryById(folderDtoResponse.getId()))
                .withSelfRel();

        Link parentLink;

        if (folderDtoResponse.getParentId() == null){
            parentLink = linkTo(methodOn(DirectoryController.class)
                    .getRootFiles())
                    .withRel("parent");
        }
        else {
            parentLink = linkTo(methodOn(DirectoryController.class)
                    .getDirectoryById(folderDtoResponse.getParentId()))
                    .withRel("parent");
        }

        Link contentLink = linkTo(methodOn(DirectoryController.class)
                .getFilesInDirectory(folderDtoResponse.getId()))
                .withRel("content");

        Link breadcrumbsLink = linkTo(methodOn(DirectoryController.class)
                .getBreadCrumbsByFolderId(folderDtoResponse.getId()))
                .withRel("breadcrumbs");

        Link uploadFileLink = linkTo(methodOn(DirectoryController.class)
                .uploadFile(null, folderDtoResponse.getId()))
                .withRel("upload_file");

        Link downloadLink = linkTo(methodOn(DirectoryController.class)
                .downloadFolder(null, folderDtoResponse.getId()))
                .withRel("download");

        folderDtoResponse.add(selfLink, parentLink, contentLink, breadcrumbsLink, uploadFileLink, downloadLink);
    }

    public void setLinksToRoot(CollectionModel<FileDtoResponse> collectionModel) throws IOException {
        Link selfLink = linkTo(methodOn(DirectoryController.class)
                .getRootFiles())
                .withSelfRel();

        Link uploadFileLink = linkTo(methodOn(DirectoryController.class)
                .uploadFileToRoot(null))
                .withRel("upload_file");

        collectionModel.add(selfLink, uploadFileLink);
    }

    public void setLinksToSearchResult(CollectionModel<FileDtoResponse> collectionModel, String name)
            throws IOException {
        Link selfLink = linkTo(methodOn(SearchController.class)
                .searchByName(name))
                .withSelfRel();

        collectionModel.add(selfLink);
    }
}

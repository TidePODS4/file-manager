package ru.server.filemanager.repository;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.server.filemanager.model.FileMetadata;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
    List<FileMetadata> findAllByParentId(UUID id);
    Optional<FileMetadata> findFileMetadataById(UUID id);
    boolean existsFileMetadataByNameAndParentId(@NotEmpty String name, UUID parentId);
}

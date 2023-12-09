package ru.server.filemanager.repository;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, UUID> {
//    List<FileMetadata> findAllByUsersContainsAndParentId(User user, UUID parentId);
//    @Query("select f from FileMetadata f join f.users u where u.id = :userId")
//    List<FileMetadata> findByUsersIdAndParentId(@Param("userId") UUID userId, UUID parentId);
    List<FileMetadata> findAllByOwnerIdAndParentId(UUID ownerId, UUID parentId);
    Optional<FileMetadata> findFileMetadataByIdAndOwnerId(UUID id, UUID ownerId);
    Optional<FileMetadata> findFileMetadataById(UUID id);
    boolean existsFileMetadataByNameAndParentId(@NotEmpty String name, UUID parentId);
    List<FileMetadata> findAllByNameContainsIgnoreCaseAndOwnerId(String name, UUID ownerId);
}

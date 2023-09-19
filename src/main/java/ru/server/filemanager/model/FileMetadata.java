package ru.server.filemanager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "parent_id")
    private UUID parentId;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "is_folder")
    private boolean isFolder;
}

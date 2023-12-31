package ru.server.filemanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "usr")
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @JsonIgnore
    @ManyToMany(mappedBy = "users")
    private Set<FileMetadata> files;

    @JsonIgnore
    @OneToMany(mappedBy = "owner")
    private Set<FileMetadata> ownFiles;
}

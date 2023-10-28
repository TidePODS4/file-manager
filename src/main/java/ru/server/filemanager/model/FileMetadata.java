package ru.server.filemanager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.hateoas.RepresentationModel;

import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "file_metadata")
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    @Nullable
    private UUID id;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "parent_id")
    private FileMetadata parent;

//    @Column(name = "parent_id", insertable = false, updatable = false)
//    private UUID parentId1;

    @Column(name = "name")
    @NotEmpty
    @Max(value = 200, message = "Name length should be less than 200 characters")
    private String name;

    @Column(name = "is_folder")
    @NotNull
    @JsonProperty(value = "is_folder")
    private boolean isFolder;

    @Column(name = "size")
    @Nullable
    private Long size;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "file_user",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> users;
}

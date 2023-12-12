package ru.server.filemanager.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import ru.server.filemanager.model.User;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDtoResponse extends RepresentationModel<FileDtoResponse> {
    private UUID id;

    @NotEmpty
    private String name;

    @NotNull
    @JsonProperty(value = "is_folder")
    private boolean isFolder;

    @Nullable
    private Long size;

    @Nullable
    @JsonIgnore
    private UUID parentId;

    @NotBlank
    @JsonProperty(value = "creation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime creationDate;

    @NotBlank
    @JsonProperty(value = "update_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private ZonedDateTime updateDate;
}

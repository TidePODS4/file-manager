package ru.server.filemanager.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FolderDtoResponse extends RepresentationModel<FileDtoResponse> {
    private UUID id;

    @NotEmpty
    private String name;

    @Nullable
    private List<FileDtoResponse> content;

    @Nullable
    @JsonIgnore
    private UUID parentId;
}

package ru.server.filemanager.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.UUID;

@Data
public class FolderDtoRequest {
    @JsonProperty(value = "parent_id")
    @Nullable
    private UUID parentId;

    @NotEmpty(message = "Name should not be empty")
    @NotBlank
    @Max(value = 200, message = "Name length should be less than 200 characters")
    private String name;
}

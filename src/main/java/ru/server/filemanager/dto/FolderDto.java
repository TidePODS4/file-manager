package ru.server.filemanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.server.filemanager.model.User;

import java.util.UUID;

@Data
public class FolderDto {
    @JsonProperty(value = "parent_id")
    private UUID parentId;

    @NotEmpty(message = "Name should not be empty")
    private String name;
}

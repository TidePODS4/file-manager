package ru.server.filemanager.config;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.server.filemanager.dto.request.FolderDtoRequest;
import ru.server.filemanager.dto.response.FileDtoResponse;
import ru.server.filemanager.dto.response.FolderDtoResponse;
import ru.server.filemanager.model.FileMetadata;
import ru.server.filemanager.service.UserService;

@Configuration
@RequiredArgsConstructor
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        modelMapper.typeMap(FolderDtoRequest.class, FileMetadata.class)
                .addMappings(
                        mapper -> mapper.map(
                                src -> true, FileMetadata::setFolder)
                );

        modelMapper.typeMap(FileMetadata.class, FileDtoResponse.class)
                .addMappings(
                        mapper -> mapper.map(src -> src.getParent().getId(), FileDtoResponse::setParentId)
                );

        modelMapper.typeMap(FileMetadata.class, FolderDtoResponse.class)
                .addMappings(
                        mapper -> mapper.map(src -> src.getParent().getId(), FolderDtoResponse::setParentId)
                );

        return modelMapper;
    }
}

package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import ru.practicum.compilation.dto.AdminCompilationDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.model.Compilation;

@Mapper
public interface CompilationMapper {

    CompilationMapper INSTANCE = Mappers.getMapper(CompilationMapper.class);

    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "events", ignore = true)
    Compilation toEntity(AdminCompilationDto adminCompilationDto);

    @Mapping(target = "events", ignore = true)
    void updateCompilationFromDto(AdminCompilationDto adminCompilationDto, @MappingTarget Compilation compilation);
}
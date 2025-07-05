package ru.practicum.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.practicum.compilation.dto.in.NewCompilationDto;
import ru.practicum.compilation.dto.in.UpdateCompilationRequest;
import ru.practicum.compilation.dto.output.CompilationDto;
import ru.practicum.compilation.model.Compilation;

@Mapper
public interface CompilationMapper {

    @Mapping(source = "events", target = "events")
    CompilationDto toDto(Compilation compilation);

    @Mapping(source = "events", target = "events")
    Compilation toEntity(NewCompilationDto newCompilationDto);

    @Mapping(source = "events", target = "events")
    Compilation updateEntity(UpdateCompilationRequest updateRequest, @MappingTarget Compilation compilation);
}

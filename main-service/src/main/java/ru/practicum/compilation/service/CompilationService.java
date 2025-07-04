package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.in.CompilationPublicParam;
import ru.practicum.compilation.dto.in.NewCompilationDto;
import ru.practicum.compilation.dto.in.UpdateCompilationRequest;
import ru.practicum.compilation.dto.output.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    public List<CompilationDto> findBy(CompilationPublicParam param) {
        log.info("Fetching compilations with params: {}", param);

        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());
        Page<Compilation> compilationPage = compilationRepository.findByPinned(param.getPinned(), pageRequest);

        List<CompilationDto> compilationDtos = compilationPage.stream()
                .map(compilationMapper::toDto)
                .toList();

        log.info("Fetched {} compilations", compilationDtos.size());
        return compilationDtos;
    }

    public CompilationDto findById(Long compId) {
        log.info("Fetching compilation by id={}", compId);

        Compilation compilation = findCompById(compId);

        CompilationDto compilationDto = compilationMapper.toDto(compilation);
        log.info("Fetched compilation: {}", compilationDto);

        return compilationDto;
    }

    public CompilationDto add(NewCompilationDto dto) {
        log.info("Creating new compilation for events: {}", dto.getEvents());

        Compilation compilationToSave = compilationMapper.toEntity(dto);
        Compilation savedCompilation = compilationRepository.save(compilationToSave);

        log.info("Compilation with id: {} was created", savedCompilation.getId());
        return compilationMapper.toDto(savedCompilation);
    }

    public void delete(Long compId) {
        log.info("Deleting compilation with id: {}", compId);

        findCompById(compId);

        compilationRepository.deleteById(compId);
        log.info("Deleted compilation with id: {}", compId);
    }

    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Updating compilation with id: {}", compId);

        Compilation findedCompilation = findCompById(compId);

        Compilation compilationToUpdate = compilationMapper.updateEntity(updateCompilationRequest, findedCompilation);

        Compilation updatedCompilation = compilationRepository.save(compilationToUpdate);

        log.info("Compilation with id: {} was updated", updatedCompilation.getId());
        return compilationMapper.toDto(updatedCompilation);
    }

    private Compilation findCompById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.warn("Compilation with id={} not found", compId);
                    return new NotFoundException("Compilation with id=" + compId + " was not found");
                });
    }
}

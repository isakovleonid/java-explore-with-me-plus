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
import ru.practicum.events.model.Event;
import ru.practicum.events.storage.EventRepository;
import ru.practicum.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    public List<CompilationDto> findBy(CompilationPublicParam param) {
        log.info("Fetching compilations with params: {}", param);

        PageRequest pageRequest = PageRequest.of(param.getFrom() / param.getSize(), param.getSize());

        Page<Compilation> compilationPage;
        if (param.getPinned() != null) {
            compilationPage = compilationRepository.findByPinned(param.getPinned(), pageRequest);
        } else {
            compilationPage = compilationRepository.findAll(pageRequest);
        }

        List<CompilationDto> compilationDtos = compilationPage.stream()
                .map(compilation -> {
                    List<Event> events = eventRepository.findAllById(compilation.getEvents().stream()
                            .map(Event::getId)
                            .toList());
                    return compilationMapper.toDto(compilation);
                })
                .toList();

        log.info("Fetched {} compilations", compilationDtos.size());
        return compilationDtos;
    }

    public CompilationDto findById(Long compId) {
        log.info("Fetching compilation by id={}", compId);

        Compilation compilation = findCompById(compId);

        List<Event> events = eventRepository.findAllById(compilation.getEvents().stream()
                .map(Event::getId)
                .toList());

        CompilationDto compilationDto = compilationMapper.toDto(compilation);
        log.info("Fetched compilation: {}", compilationDto);

        return compilationDto;
    }

    public CompilationDto add(NewCompilationDto dto) {
        log.info("Creating new compilation for events: {}", dto.getEvents());

        List<Event> events = List.of();
        if (dto.getEvents() != null) {
            events = eventRepository.findAllById(dto.getEvents());
        }

        Compilation compilationToSave = compilationMapper.toEntity(dto, events);
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

        boolean pinned = findedCompilation.getPinned();

        List<Event> events = new ArrayList<>();
        if (updateCompilationRequest.getEvents() != null) {
            events = eventRepository.findAllById(updateCompilationRequest.getEvents());
        }

        Compilation compilationToUpdate = compilationMapper.updateEntity(updateCompilationRequest, findedCompilation, events);

        if (updateCompilationRequest.getPinned() == null) {
            compilationToUpdate.setPinned(pinned);
        }

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

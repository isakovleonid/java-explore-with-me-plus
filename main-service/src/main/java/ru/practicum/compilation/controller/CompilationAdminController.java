package ru.practicum.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.in.NewCompilationDto;
import ru.practicum.compilation.dto.in.UpdateCompilationRequest;
import ru.practicum.compilation.dto.output.CompilationDto;
import ru.practicum.compilation.service.CompilationServiceImpl;

@RestController
@RequestMapping("admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationAdminController {

    private final CompilationServiceImpl compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("POST /admin/compilations {}", newCompilationDto);
        CompilationDto compilationDto = compilationService.add(newCompilationDto);
        log.info("Added new compilation for events: {}", compilationDto.getEvents());
        return compilationDto;
    }

    @DeleteMapping("/{compId}")
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("DELETE /admin/compilations/{}", compId);
        compilationService.delete(compId);
        log.info("Deleted compilation with id={}", compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        log.info("PATCH /admin/compilations/{} {}", compId, updateCompilationRequest);
        CompilationDto compilationDto = compilationService.update(compId, updateCompilationRequest);
        log.info("Updated compilation with id={}", compId);
        return compilationDto;
    }
}

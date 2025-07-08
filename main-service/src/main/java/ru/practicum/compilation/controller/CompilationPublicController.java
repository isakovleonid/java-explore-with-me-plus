package ru.practicum.compilation.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.output.CompilationDto;
import ru.practicum.compilation.dto.in.CompilationPublicParam;
import ru.practicum.compilation.service.CompilationServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationPublicController {
    private final CompilationServiceImpl compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "0") @Min(0) Integer from,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) Integer size) {
        log.info("GET /compilations with pinned={}, from={}, size={}", pinned, from, size);

        CompilationPublicParam param = new CompilationPublicParam(pinned, from, size);
        List<CompilationDto> compilations = compilationService.findBy(param);
        log.info("Returning {} compilations", compilations.size());

        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("GET /compilations/{}", compId);

        CompilationDto compilation = compilationService.findById(compId);
        log.info("Returning compilation: {}", compilation);

        return compilation;
    }
}

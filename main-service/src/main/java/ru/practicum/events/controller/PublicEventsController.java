package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.StatClient;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.events.dto.output.EventShortDto;
import ru.practicum.events.model.EventPublicParam;
import ru.practicum.events.service.EventServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Validated
@Slf4j
public class PublicEventsController {

    private final EventServiceImpl eventService;
    private final StatClient statClient;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) Set<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {

        EventPublicParam param = new EventPublicParam(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        log.info("GET /events with params: {}", param);
        List<EventShortDto> eventShorts = eventService.findEvents(param);

        log.info("HIT request \"GET /events\" to statsService with params: {}", param);
        statClient.hit(new StatisticDto(
                "main-service",
                request.getRequestURI(),
                request.getRemoteAddr(),
                LocalDateTime.now())
        );

        return eventShorts;
    }
}

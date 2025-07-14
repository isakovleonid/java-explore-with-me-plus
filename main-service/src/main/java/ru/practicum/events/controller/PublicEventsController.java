package ru.practicum.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.StatClient;
import ru.practicum.dto.in.StatisticDto;
import ru.practicum.events.dto.output.EventFullDto;
import ru.practicum.events.service.EventService;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
@Slf4j
public class PublicEventsController {
    private final StatClient statClient;
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Get event {}", eventId);

        StatisticDto statDto = StatisticDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();
        statClient.hit(statDto);
        return eventService.getEvent(eventId);
    }
}

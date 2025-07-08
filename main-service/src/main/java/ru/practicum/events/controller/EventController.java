package ru.practicum.events.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.in.EventRequestStatusUpdateRequest;
import ru.practicum.events.dto.in.NewEventDto;
import ru.practicum.events.dto.in.UpdateEventUserRequest;
import ru.practicum.events.dto.output.EventFullDto;
import ru.practicum.events.dto.output.EventShortDto;
import ru.practicum.events.dto.output.SwitchRequestsStatus;
import ru.practicum.events.service.EventServiceImpl;
import ru.practicum.requests.dto.ParticipationRequestDtoOut;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{id}/events")
@Slf4j
public class EventController {
    private final EventServiceImpl eventService;

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public SwitchRequestsStatus switchRequestsStatus(@PathVariable Long eventId,
                                                           @RequestBody @Validated EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                           @PathVariable Long id) {
        return eventService.switchRequestsStatus(eventRequestStatusUpdateRequest, eventId, id);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDtoOut> getRequests(@PathVariable Long eventId, @PathVariable Long id) {
        log.info("Get requests for event {} and user {}", eventId, id);
        return eventService.getRequests(id,eventId);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEvent(@PathVariable Long eventId, @PathVariable Long id) {
        log.info("Get event {} for user {}", eventId, id);
        return eventService.getEvent(eventId, id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable Long id,
                                             @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                             @RequestParam(required = false, defaultValue = "10")@Min(0) Integer to) {
        log.info("get events for user with id {}.", id);
        return eventService.getEventsForUser(id, from, to);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@RequestBody @Validated NewEventDto newEventDto, @PathVariable Long id) {
        log.info("create event for user with id {}.", id);
        log.info(newEventDto.toString());
        return eventService.createEvent(newEventDto, id);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(Long eventId, @RequestBody @Validated UpdateEventUserRequest updateEventUserRequest, @PathVariable Long id) {
        log.info("update event with id {} for user with id {}.", eventId, id);
        return eventService.updateEvent(updateEventUserRequest, eventId, id);
    }
}

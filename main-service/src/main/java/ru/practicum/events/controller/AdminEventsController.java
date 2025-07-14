package ru.practicum.events.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.in.UpdateEventAdminRequest;
import ru.practicum.events.dto.output.EventFullDto;
import ru.practicum.events.model.EventAdminParam;
import ru.practicum.events.model.State;
import ru.practicum.events.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/events")
public class AdminEventsController {
    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findEvents(@RequestParam(required = false) List<Long> users,
                                         @RequestParam(required = false) List<State> states,
                                         @RequestParam(required = false) List<Long> categories,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(0) Integer size) {
        EventAdminParam param = new EventAdminParam();
        param.setUsers(users);
        param.setStates(states);
        param.setCategories(categories);
        param.setStart(start);
        param.setEnd(end);
        param.setFrom(from == null ? 0 : from);
        param.setSize(size == null ? 10 : size);

        return eventService.findEvents(param);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto editEvent(@RequestBody UpdateEventAdminRequest request, @PathVariable Long eventId) {
        return eventService.updateEvent(request, eventId);
    }
}

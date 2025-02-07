package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.dto.EventDtoGetParam;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventService;
    @GetMapping
    public List<EventShortDto> getPublicEvents(@ModelAttribute EventDtoGetParam prm,
                                                HttpServletRequest rqt) {
        return eventService.getPublicEvents(prm, rqt);
    }
}

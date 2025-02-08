package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventDtoGetParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.model.UpdateEventUserRequest;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(Long userId, NewEventDto newEventDto);
    List<EventShortDto> getEventsForUser(EventDtoGetParam prm);
    EventFullDto getEventByIdForUser(EventDtoGetParam prm);
    List<EventFullDto> getEventsForAdmin(EventDtoGetParam prm);
    EventFullDto updateEventByAdmin(Long id, UpdateEventAdminRequest rq);
    EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest rq);
    List<EventShortDto> getPublicEvents(EventDtoGetParam prm, HttpServletRequest rqt);
    EventFullDto getPublicEventById(Long id, HttpServletRequest rqt);
}

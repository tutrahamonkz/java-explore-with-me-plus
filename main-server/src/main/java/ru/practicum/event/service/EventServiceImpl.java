package ru.practicum.event.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventDtoGetParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.service.UserService;


import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventMapper eventMapper;

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = eventMapper.toEntity(newEventDto);
        event.setLocation(locationService.getLocation(event.getLocation())); //сохранение новой location в базу и установка в event
        event.setCategory(categoryService.getCategory(event.getCategory().getId())); //получение категории по пришедшему id
        event.setInitiator(userService.getUserById(userId));
        log.info("Создание события {}", event);
        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    public List<EventShortDto> getEventsForUser(EventDtoGetParam eventDtoGetParam) {
        log.info("Получение списка мероприятий для пользователя с id {} ", eventDtoGetParam.getUserId());
        QEvent event = QEvent.event;
        Predicate predicate = eventPredicate(eventDtoGetParam, event);
        PageRequest pageRequest = PageRequest.of(eventDtoGetParam.getFrom(), eventDtoGetParam.getSize());
        List<Event> events;
        if (predicate == null) {
            events = eventRepository.findAll(pageRequest).getContent();
        } else {
            events = eventRepository.findAll(predicate, pageRequest).getContent();
        }
        return eventMapper.toEventShortDto(events);
    }

    private Predicate eventPredicate(EventDtoGetParam eventDtoGetParam, QEvent event) {
        if (eventDtoGetParam.getUserId() == null) {
            return null;
        }
        return event.initiator.id.in(eventDtoGetParam.getUserId());
    }
}

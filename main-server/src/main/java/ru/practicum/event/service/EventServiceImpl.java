package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.user.service.UserService;

@Service
@RequiredArgsConstructor
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
        event = eventRepository.save(event);
        return eventMapper.toEventFullDto(event);
    }

}

package ru.practicum.event.service;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
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
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.user.service.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final EventMapper mp;
    private final QEvent event = QEvent.event;

    @Transactional
    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        Event event = mp.toEntity(newEventDto);
        event.setLocation(locationService.getLocation(event.getLocation())); //сохранение новой location в базу и установка в event
        event.setCategory(categoryService.getCategory(event.getCategory().getId())); //получение категории по пришедшему id
        event.setInitiator(userService.getUserById(userId));
        log.info("Создание события {}", event);
        return mp.toEventFullDto(eventRepository.save(event));
    }

    public List<EventShortDto> getEventsForUser(EventDtoGetParam prm) {
        log.info("Получение списка мероприятий для пользователя с id {} ", prm.getUserId());
        Predicate predicate = event.initiator.id.eq(prm.getUserId());
        PageRequest pageRequest = PageRequest.of(prm.getFrom(), prm.getSize());
        List<Event> events = eventRepository.findAll(predicate, pageRequest).getContent();
        return mp.toEventShortDto(events);
    }

    public EventFullDto getEventByIdForUser(EventDtoGetParam prm) {
        Predicate predicate = event.initiator.id.eq(prm.getUserId())
                .and(event.id.eq(prm.getEventId()));
        PageRequest pageRequest = PageRequest.of(prm.getFrom(), prm.getSize());
        Event searchEvent = eventRepository.findOne(predicate).
                orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id %d для пользователя с id %d не найдено.",
                                prm.getEventId(), prm.getUserId())));
        return mp.toEventFullDto(searchEvent);
    }

    public List<EventFullDto> getEventsForAdmin(EventDtoGetParam prm) {
        dataValid(prm.getRangeStart(), prm.getRangeEnd());
        //Predicate predicate = Expressions.asBoolean(true).isTrue();
        Predicate predicate=null;
        if (prm.getUsers() != null && !prm.getUsers().isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.initiator.id.in(prm.getUsers()));
        }
        if (prm.getStates() != null && !prm.getStates().isEmpty()) {
           /* List<State> states = prm.getStates().stream()
                    .map(State::valueOf) // Преобразуем строки в перечисление
                    .toList();
            predicate = ExpressionUtils.and(predicate, event.state.in(states));*/
            StringExpression stateAsString = Expressions.stringTemplate("CAST({0} AS string)", event.state);
            predicate = ExpressionUtils.and(predicate, stateAsString.in(prm.getStates()));
        }
        if (prm.getCategories() != null && !prm.getCategories().isEmpty()) {
            predicate = ExpressionUtils.and(predicate,event.category.id.in(prm.getCategories()));
        }
        if (prm.getRangeStart() != null && prm.getRangeEnd() != null) {
            predicate = ExpressionUtils.and(predicate, event.eventDate.between(prm.getRangeStart(), prm.getRangeEnd()));
        }
        PageRequest pageRequest = PageRequest.of(prm.getFrom(), prm.getSize());
        List<Event> events = eventRepository.findAll(predicate, pageRequest).getContent();
        return mp.toEventFullDto(events);
    }


    private void dataValid(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart == null || rangeEnd == null) {
            return;
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Некорректно указана даты, когда должны произойти события");
        }
    }
}

package ru.practicum.event.service;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.service.CategoryService;
import ru.practicum.event.dto.EventDtoGetParam;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.QEvent;
import ru.practicum.event.model.State;
import ru.practicum.event.model.StateAction;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.model.UpdateEventUserRequest;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictStateException;
import ru.practicum.exception.ConflictTimeException;
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
    private final LocationMapper lmp;
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

    @Override
    public List<EventShortDto> getEventsForUser(EventDtoGetParam prm) {
        log.info("Получение списка мероприятий для пользователя с id {} ", prm.getUserId());
        Predicate predicate = event.initiator.id.eq(prm.getUserId());
        PageRequest pageRequest = PageRequest.of(prm.getFrom(), prm.getSize());
        List<Event> events = eventRepository.findAll(predicate, pageRequest).getContent();
        //TODO: добавить views
        return mp.toEventShortDto(events);
    }

    @Override
    public EventFullDto getEventByIdForUser(EventDtoGetParam prm) {
        Predicate predicate = event.initiator.id.eq(prm.getUserId())
                .and(event.id.eq(prm.getEventId()));
        PageRequest pageRequest = PageRequest.of(prm.getFrom(), prm.getSize());
        Event searchEvent = eventRepository.findOne(predicate).
                orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id %d для пользователя с id %d не найдено.",
                                prm.getEventId(), prm.getUserId())));
        //TODO: добавить views
        log.info("Получение события с id {}  для пользователя с id {}", prm.getEventId(), prm.getUserId());
        return mp.toEventFullDto(searchEvent);
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(EventDtoGetParam prm) {
        Predicate predicate = null;
        if (prm.getUsers() != null && !prm.getUsers().isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.initiator.id.in(prm.getUsers()));
        }
        if (prm.getStates() != null && !prm.getStates().isEmpty()) {
            List<State> states = prm.getStates().stream()
                    .map(State::valueOf) // Преобразуем строки в перечисление
                    .toList();
            predicate = ExpressionUtils.and(predicate, event.state.in(states));
        }
        if (prm.getCategories() != null && !prm.getCategories().isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.category.id.in(prm.getCategories()));
        }
        if (prm.getRangeStart() != null && prm.getRangeEnd() != null) {
            predicate = ExpressionUtils.and(predicate, event.eventDate.between(prm.getRangeStart(), prm.getRangeEnd()));
        }
        PageRequest pageRequest = PageRequest.of(prm.getFrom(), prm.getSize());
        List<Event> events = eventRepository.findAll(predicate, pageRequest).getContent();
        //TODO: добавить views
        log.info("Получение списка событий администратором с параметрами {} и предикатом {}", prm, predicate);
        return mp.toEventFullDto(events);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long id, UpdateEventAdminRequest rq) {
        Event event = eventRepository.findById(id).
                orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id %d не найдено.", id)));
        if ((rq.getStateAction() == StateAction.PUBLISH_EVENT && event.getState() != State.PENDING) ||
                (rq.getStateAction() == StateAction.REJECT_EVENT && event.getState() != State.PUBLISHED)) {
            throw new ConflictStateException(
                    (rq.getStateAction() == StateAction.PUBLISH_EVENT) ?
                            "Невозможно опубликовать событие, так как текущий статус не PENDING"
                            : "Нельзя отменить публикацию, так как событие уже опубликовано");
        }
        if (rq.getLocation() != null) {
            event.setLocation(locationService.getLocation(lmp.toLocation(rq.getLocation())));
        }
        mp.updateFromAdmin(rq, event);
        event.setState(rq.getStateAction() == StateAction.PUBLISH_EVENT ? State.PUBLISHED : State.CANCELED);
        return mp.toEventFullDto(eventRepository.saveAndFlush(event));
    }

    @Override
    @Transactional
    public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest rq) {
        if (rq.getEventDate() != null && rq.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ConflictTimeException("Время не может быть раньше, через два часа от текущего момента");
        }
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).
                orElseThrow(() -> new NotFoundException(
                        String.format("Событие с id %d для пользователя с id %d не найдено.", eventId, userId)));
        if (event.getState() == State.PUBLISHED) {
            throw new ConflictStateException("Изменить можно только неопубликованное событие");
        }
        if (rq.getStateAction() != null) {
            switch (rq.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(State.PENDING);
                case CANCEL_REVIEW -> event.setState(State.CANCELED);
                default -> throw new IllegalArgumentException("Неизвестный статус: " + rq.getStateAction());
            }
        }
        if (rq.getLocation() != null) {
            event.setLocation(locationService.getLocation(lmp.toLocation(rq.getLocation())));
        }
        mp.updateFromUser(rq, event);
        return mp.toEventFullDto(eventRepository.saveAndFlush(event));
    }

    @Override
    public List<EventShortDto> getPublicEvents(EventDtoGetParam prm, HttpServletRequest rqt) {
        Predicate predicate = event.state.eq(State.PUBLISHED);
        if (prm.getText() != null) {
            predicate = ExpressionUtils.and(predicate, (event.annotation.contains(prm.getText().toLowerCase())).or(
                    event.description.contains(prm.getText().toLowerCase())));
        }
        if (prm.getCategories() != null && !prm.getCategories().isEmpty()) {
            predicate = ExpressionUtils.and(predicate, event.category.id.in(prm.getCategories()));
        }
        if (prm.getPaid() != null) {
            predicate = ExpressionUtils.and(predicate, event.paid.eq(prm.getPaid()));
        }
        if (prm.getRangeEnd() != null && prm.getRangeEnd() != null) {
            predicate = ExpressionUtils.and(predicate, event.eventDate.between(prm.getRangeStart(), prm.getRangeEnd()));
        } else {
            predicate = ExpressionUtils.and(predicate, event.eventDate.gt(LocalDateTime.now())); //TODO: проверить
        }
        if (prm.getOnlyAvailable() != null && prm.getOnlyAvailable()) { //проверка есть ли еще места на мероприятие
            predicate = ExpressionUtils.and(predicate, (event.participantLimit.eq(0)).or(
                    event.participantLimit.subtract(event.confirmedRequests).gt(0)));
        }
        Sort sort = Sort.unsorted();
        if(prm.getSort().equals("EVENT_DATE")) {
            sort = Sort.by(Sort.Direction.ASC, "eventDate");
        }else if (prm.getSort().equals("VIEWS")) {
            sort = Sort.by(Sort.Direction.DESC, "views");
        }
        PageRequest pageRequest = PageRequest.of(prm.getFrom(), prm.getSize(), sort);
        List<Event> events = eventRepository.findAll(predicate, pageRequest).getContent();
    }

/*
    private void dataValid(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart == null || rangeEnd == null) {
            return;
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Некорректно указана даты, когда должны произойти события");
        }
    }
    */

//TODO: проверить getEventsForAdmin тесты идут с параметрами == 0
}


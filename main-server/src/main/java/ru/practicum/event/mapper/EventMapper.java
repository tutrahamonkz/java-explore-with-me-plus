package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;


@Mapper(componentModel = "spring")
public interface EventMapper {
    //target - поле на выходе, source на входе
    @Mapping(target = "views", expression = "java(event.getViews() == null ? 0 : event.getViews().size())")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "views", expression = "java(event.getViews() != null ? event.getViews().size() : 0)")
        // Подсчёт просмотров
    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(ru.practicum.event.model.State.PENDING)")
    @Mapping(target = "participantLimit", source = "participantLimit", defaultValue = "0")
    Event toEntity(NewEventDto newEventDto);
}

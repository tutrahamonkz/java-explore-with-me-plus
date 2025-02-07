package ru.practicum.event.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.UpdateEventAdminRequest;
import ru.practicum.event.model.UpdateEventUserRequest;

import java.util.List;


@Mapper(componentModel = "spring", uses = {LocationMapper.class})
public interface EventMapper {
    //target - поле на выходе, source на входе
    @Mapping(target = "views", expression = "java(event.getViews() == null ? 0 : event.getViews().size())")
    EventShortDto toEventShortDto(Event event);

    @Mapping(target = "views", expression = "java(event.getViews() != null ? event.getViews().size() : 0)")

    EventFullDto toEventFullDto(Event event);

    @Mapping(target = "category.id", source = "category")
    @Mapping(target = "createdOn", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "state", expression = "java(ru.practicum.event.model.State.PENDING)")
    @Mapping(target = "participantLimit", source = "participantLimit", defaultValue = "0")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    Event toEntity(NewEventDto newEventDto);

    List<EventShortDto> toEventShortDto(List<Event> events);
    List<EventFullDto> toEventFullDto(List<Event> events);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE) //игнорирование полей c null
    @Mapping(target = "category.id", source = "category")
    void updateFromAdmin(UpdateEventAdminRequest rq, @MappingTarget Event event); //@MappingTarget указывает что целевой объект, в который будут копироваться значения, уже существует и будет обновлен

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category.id", source = "category")
    void updateFromUser(UpdateEventUserRequest rq, @MappingTarget Event event);
}

package ru.practicum.compilation.dto;

import lombok.Getter;
import ru.practicum.event.model.Event;

import java.util.List;

@Getter
public class CompilationDto {

    private List<Event> events;

    private Long id;

    private Boolean pinned;

    private String title;
}
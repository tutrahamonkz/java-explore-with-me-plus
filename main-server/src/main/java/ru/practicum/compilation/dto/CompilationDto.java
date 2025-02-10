package ru.practicum.compilation.dto;

import lombok.Getter;
import ru.practicum.event.dto.EventShortDto;

import java.util.List;

@Getter
public class CompilationDto {

    private List<EventShortDto> events;

    private Long id;

    private Boolean pinned;

    private String title;
}
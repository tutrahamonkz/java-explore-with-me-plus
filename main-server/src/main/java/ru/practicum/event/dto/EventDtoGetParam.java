package ru.practicum.event.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class EventDtoGetParam {
    private Integer size = 10;
    private Integer from = 0;
    private Long userId;
}

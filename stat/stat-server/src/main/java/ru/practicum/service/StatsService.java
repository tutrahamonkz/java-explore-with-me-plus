package ru.practicum.service;


import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatsDto saveRequest(StatsDto statDto);

    List<StatsDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}